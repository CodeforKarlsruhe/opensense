package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import android.view.View
import android.widget.ImageView
import com.androidplot.ui.Anchor
import com.androidplot.ui.HorizontalPositioning
import com.androidplot.ui.Size
import com.androidplot.ui.VerticalPositioning
import com.androidplot.xy.*
import de.codefor.karlsruhe.opensense.R.id.plot_widget_img
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget
import java.util.logging.Logger
import kotlinx.android.synthetic.main.plot_widget.*
import kotlinx.android.synthetic.main.plot_widget.view.*
import java.sql.Array


class PlotWidget : BaseWidget() {

    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    companion object {

        val LOG = Logger.getLogger(this::class.java.toString())

        fun update(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
            WidgetHelper.getSensorHistory(context, appWidgetId).subscribe({ sensorHist ->
                drawPlot(context, appWidgetId, appWidgetManager, sensorHist)
            })
        }

        private fun drawPlot(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager, sensorHist: List<SensorHistory>) {
            val views = RemoteViews(context.getPackageName(), R.layout.plot_widget);

            val plot = XYPlot(context, "History TODO")

            val h = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            val w = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)

            plot.graph.setMargins(0f, 0f, 0f, 0f)
            plot.graph.setPadding(0f, 0f, 0f, 0f)

            plot.getGraph().position(0f, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0f,
                    VerticalPositioning.ABSOLUTE_FROM_TOP, Anchor.LEFT_TOP)

            plot.getGraph().setSize(Size.FILL)

            plot.getLayoutManager().moveToTop(plot.getTitle())

            plot.measure(w, h);
            plot.layout(0, 0, w, h);

            LOG.info("sensorHistory size: " + sensorHist.size + ", data: " + sensorHist.toString())

            val history = mutableListOf<Double>()

            for (dataPoint in sensorHist) {
                history.add(dataPoint.value ?: Double.NaN)
                // TODO add timestamps as X axis info
               // history.add(dataPoint.createdAt.)
            }

            // Turn the above arrays into XYSeries':
            // TODO probably better to use TimeSeriesPlot
            val series1 = SimpleXYSeries(
                    history,          // SimpleXYSeries takes a List so turn our array into a List
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                    "sensor type TODO")                             // Set the display title of the series

            // TODO Create a proper formatter to use for drawing a series using LineAndPointRenderer:
            val series1Format = LineAndPointFormatter(
                    Color.rgb(0, 200, 0),                   // line color
                    Color.rgb(0, 100, 0),                   // vertex color
                    null,                   // fill color
                    null)                                 // pointlabelformatter (none)

            // add a new series' to the xyplot:
            plot.addSeries(series1, series1Format)
           // plot.getLegend().setVisible(true)

            plot.setLinesPerRangeLabel(3)
            plot.setLinesPerDomainLabel(2)
            // TODO show and set range labels (x, y labels)
            plot.setRangeLabel("Temp. in Grad")

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

            plot.draw(Canvas(bitmap))

            views.setImageViewBitmap(R.id.plot_widget_img, bitmap)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


    }
}
