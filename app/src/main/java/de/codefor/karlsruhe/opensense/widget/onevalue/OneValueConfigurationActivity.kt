package de.codefor.karlsruhe.opensense.widget.onevalue

import android.appwidget.AppWidgetManager
import android.content.Context
import de.codefor.karlsruhe.opensense.widget.base.BaseWidgetConfigurationActivity

class OneValueConfigurationActivity : BaseWidgetConfigurationActivity() {
    init {
        maxSensorItems = 1
    }

    override fun update(context: Context, widgetId: Int, appWidgetManager: AppWidgetManager) {
        OneValueWidget.update(context, widgetId, appWidgetManager)
    }
}