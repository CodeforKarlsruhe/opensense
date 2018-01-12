package de.codefor.karlsruhe.opensense.widget.onevalue

import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.View
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
            val views = RemoteViews(context.packageName, R.layout.one_value_widget)
            //Show progress bar, hide refresh button
            views.apply {
                setViewVisibility(R.id.one_value_widget_refresh_button, View.INVISIBLE)
                setViewVisibility(R.id.one_value_widget_progress_bar, View.VISIBLE)
                setProgressBar(R.id.one_value_widget_progress_bar, 100, 0, true)
            }
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)

            WidgetHelper.getSenseBox(context, appWidgetId).subscribe({ senseBox ->
                val sensorIds = WidgetHelper.loadSensorIds(context, appWidgetId)
                val sensor = senseBox.sensors?.first { (id) -> id == sensorIds.first() }
                views.apply {
                    //Show refresh button, hide progress bar
                    setViewVisibility(R.id.one_value_widget_refresh_button, View.VISIBLE)
                    setViewVisibility(R.id.one_value_widget_progress_bar, View.GONE)
                    //Update values
                    setTextViewText(R.id.one_value_widget_box_name, senseBox.name)
                    setTextViewText(R.id.one_value_widget_sensor_data, "${sensor?.lastMeasurement?.value} ${sensor?.unit}")
                    setTextViewText(R.id.one_value_widget_sensor_title, sensor?.title)
                }

                setOnClickPendingIntents(context, appWidgetId, views)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }, {
                views.apply {
                    //Show refresh button, hide progress bar
                    setViewVisibility(R.id.one_value_widget_refresh_button, View.VISIBLE)
                    setViewVisibility(R.id.one_value_widget_progress_bar, View.GONE)
                    //Remove values, set error text
                    setTextViewText(R.id.one_value_widget_box_name, "")
                    setTextViewText(R.id.one_value_widget_sensor_data, context.getString(R.string.loading_error_text))
                    setTextViewText(R.id.one_value_widget_sensor_title, "")
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