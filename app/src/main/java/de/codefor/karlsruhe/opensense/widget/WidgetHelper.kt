package de.codefor.karlsruhe.opensense.widget

import android.content.Context
import de.codefor.karlsruhe.opensense.data.OpenSenseMapService
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call

object WidgetHelper {
    private val PREFS_NAME = "de.codefor.karlsruhe.opensense.widget"
    private val PREF_BOX_ID = "box_id_"
    private val PREF_SENSOR_IDS = "sensor_ids_"

    internal fun saveConfiguration(context: Context, appWidgetId: Int, boxId: String, sensors: List<Sensor>) {
        val sensorIds = mutableListOf<String>()
        sensors.forEach { it.id?.let { it1 -> sensorIds.add(it1) } }

        context.getSharedPreferences(PREFS_NAME, 0)
                .edit()
                .putString(PREF_BOX_ID + appWidgetId, boxId)
                .putStringSet(PREF_SENSOR_IDS + appWidgetId, sensorIds.toSet())
                .apply()
    }

    internal fun deleteConfiguration(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, 0)
                .edit()
                .remove(PREF_BOX_ID + appWidgetId)
                .remove(PREF_SENSOR_IDS + appWidgetId)
                .apply()
    }

    internal fun loadBoxId(context: Context, appWidgetId: Int): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(PREF_BOX_ID + appWidgetId, "")
    }

    internal fun loadSensorIds(context: Context, appWidgetId: Int): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getStringSet(PREF_SENSOR_IDS + appWidgetId, emptySet()).toList()
    }

    internal fun getSenseBox(context: Context, appWidgetId: Int): Single<SenseBox> {
        val boxId = loadBoxId(context, appWidgetId)
        return getSenseBox(boxId)
    }

    internal fun getSenseBox(boxId: String): Single<SenseBox> {
        if (boxId.isEmpty()) return Single.error { Exception() }

        return OpenSenseMapService.getBox(boxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    internal fun getAllBoxes(): Single<List<SenseBox>> {
        return OpenSenseMapService.getAllBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    internal fun formatSensorData(value: String?, unit: String?): String {
        return "$value $unit"
    }
}
