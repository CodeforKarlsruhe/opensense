package de.codefor.karlsruhe.opensense.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import de.codefor.karlsruhe.opensense.data.OpenSenseMapService
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget
import de.codefor.karlsruhe.opensense.widget.base.BaseWidgetConfigurationActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KClass

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

    internal fun getSensorHistory(context: Context, appWidgetId: Int): Single<List<SensorHistory>> {
        val boxId = loadBoxId(context, appWidgetId)
        // TODO make configurable, the following line crashes
        //val sensorId = loadSensorIds(context, appWidgetId).first()
        val sensorId = "59c67b5ed67eb50011666dc0"
        Log.i("WidgetHelper", "getSensorHistory() boxId: $boxId, sensorId: $sensorId")
        return getSensorHistory(boxId, sensorId)
    }

    private fun getSensorHistory(boxId: String, sensorId: String): Single<List<SensorHistory>> {
        return OpenSenseMapService.getSensorHistory(boxId, sensorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    internal fun createConfigurationPendingIntent(context: Context,
                                                  appWidgetId: Int,
                                                  configActivity: KClass<out BaseWidgetConfigurationActivity>): PendingIntent {
        val intent = Intent(context, configActivity.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        return PendingIntent.getActivity(context, appWidgetId, intent, 0)
    }

    internal fun createRefreshPendingIntent(context: Context,
                                            appWidgetId: Int,
                                            appWidgetProvider: KClass<out BaseWidget>): PendingIntent {
        val intent = Intent(context, appWidgetProvider.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, IntArray(1, { appWidgetId }))
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        return PendingIntent.getBroadcast(context, appWidgetId, intent, 0)
    }
}
