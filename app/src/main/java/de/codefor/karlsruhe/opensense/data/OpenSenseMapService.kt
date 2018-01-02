package de.codefor.karlsruhe.opensense.data

import android.util.Log
import com.squareup.moshi.Moshi
import de.codefor.karlsruhe.opensense.data.boxes.BoxesApi
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

typealias SensorData = Pair<Sensor, List<SensorHistory>>

object OpenSenseMapService {
    private val boxesApi: BoxesApi

    init {
        val moshi = Moshi.Builder().add(DateTime::class.java, DateTimeAdapter()).build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.opensensemap.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        boxesApi = retrofit.create(BoxesApi::class.java)
    }

    fun getBox(boxId: String): Single<SenseBox> {
        return boxesApi.getBox(boxId)
    }

    fun getAllBoxes(): Single<List<SenseBox>> {
        return boxesApi.getAllBoxes()
    }

    fun getSenseBoxAndSensorData(boxId: String, sensorId: String): Single<Pair<SenseBox, SensorData>> {
        Log.i("OpenSenseMapService", "getSenseBoxAndSensorData() boxId: $boxId, sensorId: $sensorId")
        return boxesApi.getBox(boxId).zipWith(boxesApi.getSensorHistory(boxId, sensorId),
                BiFunction<SenseBox, List<SensorHistory>, Pair<SenseBox, SensorData>> { senseBox, sensorHistory ->
                    val sensor = senseBox.sensors?.first { it.id == sensorId }
                            ?: throw IllegalStateException("The box $boxId doesn't contain the sensor id $sensorId")
                    Pair(senseBox, SensorData(sensor, sensorHistory))
                })
    }
}