package de.codefor.karlsruhe.opensense.widget.onevalue

import android.appwidget.AppWidgetManager
import android.content.Context
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

                val views = RemoteViews(context.packageName, R.layout.one_value_widget)
                views.apply {
                    setTextViewText(R.id.one_value_widget_sensor_title, sensor?.title)
                    setTextViewText(R.id.one_value_widget_sensor_data, "${sensor?.lastMeasurement?.value} ${sensor?.unit}")
                    setTextViewText(R.id.one_value_widget_box_name, senseBox.name)
                }

                setOnClickPendingIntents(context, appWidgetId, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }, {
                val views = RemoteViews(context.packageName, R.layout.one_value_widget)
                views.apply {
                    setTextViewText(R.id.one_value_widget_sensor_title, "")
                    setTextViewText(R.id.one_value_widget_sensor_data, context.getString(R.string.one_value_error_text))
                    setTextViewText(R.id.one_value_widget_box_name, "")
                }

                setOnClickPendingIntents(context, appWidgetId, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            })
        }

        private fun setOnClickPendingIntents(context: Context, appWidgetId: Int, views: RemoteViews) {
            views.setOnClickPendingIntent(
                    R.id.one_value_widget_configuration_button,
                    WidgetHelper.createConfigurationPendingIntent(context, appWidgetId, OneValueConfigurationActivity::class)
            )

            views.setOnClickPendingIntent(
                    R.id.one_value_widget_refresh_button,
                    WidgetHelper.createRefreshPendingIntent(context, appWidgetId, OneValueWidget::class)
            )
        }
    }
}