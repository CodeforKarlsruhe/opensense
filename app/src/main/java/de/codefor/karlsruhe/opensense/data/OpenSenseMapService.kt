package de.codefor.karlsruhe.opensense.data

import de.codefor.karlsruhe.opensense.data.boxes.BoxesApi
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


object OpenSenseMapService {
    private val boxesApi: BoxesApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.opensensemap.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        boxesApi = retrofit.create(BoxesApi::class.java)
    }

    fun getBox(boxId: String): Single<SenseBox> {
        return boxesApi.getBox(boxId)
    }
}