package de.codefor.karlsruhe.opensense.widget.onevalue

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget


class OneValueWidget : BaseWidget() {
    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    companion object {
        fun update(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
            WidgetHelper.getSenseBox(context, appWidgetId).subscribe({ senseBox ->
                val sensorIds = WidgetHelper.loadSensorIds(context, appWidgetId)
                val sensor = senseBox.sensors?.first { (id) -> id == sensorIds.first() }

                val text = WidgetHelper.formatSensorData(sensor?.lastMeasurement?.value, sensor?.unit)
                val views = RemoteViews(context.packageName, R.layout.one_value_widget)
                views.apply {
                    setTextViewText(R.id.one_value_widget_sensor_title, sensor?.title)
                    setTextViewText(R.id.one_value_widget_sensor_data, text)
                    setTextViewText(R.id.one_value_widget_box_name, senseBox.name)
                }

                val intent = Intent(context, OneValueConfigurationActivity::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, 0)
                views.setOnClickPendingIntent(R.id.one_value_widget_configuration_button, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }, {
                // TODO: Hide needless views or show different layout
                val views = RemoteViews(context.packageName, R.layout.one_value_widget)
                views.setTextViewText(R.id.one_value_widget_sensor_data, context.getString(R.string.one_value_error_text))
                appWidgetManager.updateAppWidget(appWidgetId, views)
            })
        }
    }
}