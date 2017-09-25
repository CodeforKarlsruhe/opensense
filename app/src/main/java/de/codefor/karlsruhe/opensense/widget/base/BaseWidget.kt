package de.codefor.karlsruhe.opensense.widget.base

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import de.codefor.karlsruhe.opensense.widget.WidgetHelper

/**
 * Implementation of default widget containing a maximum of five sensor data.
 * The configuration is implemented in [BaseWidgetConfigurationActivity].
 */
abstract class BaseWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            onUpdateWidget(context, appWidgetId, appWidgetManager)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetHelper.deleteConfiguration(context, appWidgetId)
        }
    }

    abstract fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager)
}

