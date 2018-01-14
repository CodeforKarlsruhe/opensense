package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import de.codefor.karlsruhe.opensense.widget.base.BaseWidgetConfigurationActivity

class PlotWidgetConfigurationActivity : BaseWidgetConfigurationActivity() {
    init {
        maxSensorItems = 1
    }

    override fun update(widgetId: Int) {
        PlotWidget.update(this, widgetId, AppWidgetManager.getInstance(this))
    }
}