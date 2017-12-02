package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import com.androidplot.xy.XYSeries
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYPlot
import android.view.View
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget
import java.util.*
import java.util.logging.Logger


class PlotWidget : BaseWidget() {

    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    companion object {
        fun update(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {

            val views = RemoteViews(context.getPackageName(), R.layout.plot_widget);

            val plot = XYPlot(context, "History TODO");
            //plot.getLayoutParams().height = 100;
            //plot.getLayoutParams().width = 100;

            //plot.measure(150,150)
            //plot.layout(0,0,150,150)
            plot.setDrawingCacheEnabled(true)

            WidgetHelper.getSensorHistory(context, appWidgetId).subscribe({ sensorHist ->
               // val sensorIds = WidgetHelper.loadSensorIds(context, appWidgetId)
               // val sensor = senseBox.sensors?.first { (id) -> id == sensorIds.first() }
               val history = doubleArrayOf()
                for (idx in 0..sensorHist.size) {
                    history.set(idx, sensorHist[idx].value)
                    Logger.getLogger("loggerTest").info(sensorHist[idx].value.toString())
                }
                // Turn the above arrays into XYSeries':
                val series1 = SimpleXYSeries(
                        history.asList(),          // SimpleXYSeries takes a List so turn our array into a List
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                        "sensor type TODO");                             // Set the display title of the series

                // TODO Create a proper formatter to use for drawing a series using LineAndPointRenderer:
                val series1Format = LineAndPointFormatter(
                      Color.rgb(0, 200, 0),                   // line color
                     Color.rgb(0, 100, 0),                   // vertex color
                     Color.rgb(0, 100, 0),                   // fill color
                     null);                                  // pointlabelformatter (none)

                // add a new series' to the xyplot:
                plot.addSeries(series1, series1Format);

                val bmp = plot.getDrawingCache()
                views.setBitmap(R.id.plot_widget_img, "setImageBitmap", bmp)

                views.setTextViewText(R.id.plot_widget_box_name, "testbla")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }, {
                views.apply {
                    //Remove values, set error text
                   // setTextViewText(R.id.one_value_widget_box_name, "")
                   // setTextViewText(R.id.one_value_widget_sensor_data, context.getString(R.string.one_value_error_text))
                   // setTextViewText(R.id.one_value_widget_sensor_title, "")
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            })


        }
    }
}