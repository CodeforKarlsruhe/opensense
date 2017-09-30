package de.codefor.karlsruhe.opensense.widget.onevalue

import android.appwidget.AppWidgetManager
import de.codefor.karlsruhe.opensense.widget.base.BaseWidgetConfigurationActivity

class OneValueConfigurationActivity : BaseWidgetConfigurationActivity() {
    init {
        maxSensorItems = 1
    }

    override fun update(widgetId: Int) {
        OneValueWidget.update(this, widgetId, AppWidgetManager.getInstance(this))
    }
}