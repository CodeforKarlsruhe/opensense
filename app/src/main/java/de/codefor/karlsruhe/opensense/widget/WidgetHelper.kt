package de.codefor.karlsruhe.opensense.widget

import android.content.Context
import de.codefor.karlsruhe.opensense.data.OpenSenseMapService
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object WidgetHelper {
    private val PREFS_NAME = "de.codefor.karlsruhe.opensense.widget"
    private val PREF_BOX_ID = "box_id_"
    private val PREF_SENSOR_IDS = "sensor_ids_"

    internal fun saveConfiguration(context: Context, appWidgetId: Int, boxId: String, sensorIds: List<String>) {
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

    internal fun getSenseBox(context: Context, appWidgetId: Int): Single<SenseBox> {
        val boxId = loadBoxId(context, appWidgetId)
        if (boxId.isEmpty()) return Single.error { Exception() }

        return OpenSenseMapService.getBox(boxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    internal fun getSensorList(boxId: String): Single<List<Sensor>> {
        return OpenSenseMapService.getBox(boxId)
                .map { result -> result.sensors ?: emptyList() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
