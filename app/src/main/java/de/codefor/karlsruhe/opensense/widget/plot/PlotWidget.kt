package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import android.view.View
import com.androidplot.xy.*
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget
import java.util.logging.Logger
import com.androidplot.xy.LineAndPointFormatter

class PlotWidget : BaseWidget() {

    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    companion object {

        val LOG = Logger.getLogger(this::class.java.toString())


        fun update(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
            val views = RemoteViews(context.packageName, R.layout.plot_widget)

            //Show progress bar, hide refresh button
            views.apply {
                setViewVisibility(R.id.plot_widget_refresh_button, View.INVISIBLE)
                setViewVisibility(R.id.plot_widget_progress_bar, View.VISIBLE)
                setProgressBar(R.id.plot_widget_progress_bar, 100, 0, true)
            }
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)

            WidgetHelper.getSensorHistory(context, appWidgetId).subscribe(
                    // onSuccess
                    { sensorHist ->
                        drawPlot(context, appWidgetId, appWidgetManager, sensorHist)
                    },
                    // onError: do nothing (but also do not crash!)
                    { }
            )
        }

        private fun drawPlot(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager, sensorHist: List<SensorHistory>) {
            val views = RemoteViews(context.getPackageName(), R.layout.plot_widget)

            val plot = XYPlot(context, "Historyplot")

            // TODO show and set x- and y-tics
            // TODO use proper strings depending on selected sensor
            plot.setRangeLabel("Temp. in Grad")
            plot.setDomainLabel("Zeit")

            plot.title.labelPaint.textSize = 20f
            plot.rangeTitle.labelPaint.textSize = 20f
            plot.domainTitle.labelPaint.textSize = 20f

            plot.legend.isVisible = false

            plot.setBackgroundColor(Color.TRANSPARENT)

            val h = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            val w = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            plot.setPlotMargins(10f, 10f, 10f, 10f)

            plot.layout(0, 0, w, h)

            LOG.info("sensorHistory size: " + sensorHist.size + ", data: " + sensorHist.toString())

            val history = mutableListOf<Double>()

            for (dataPoint in sensorHist) {
                history.add(dataPoint.value ?: Double.NaN)
                // TODO add timestamps as X axis info
                // needs a proper handling of timestamps earlier (in OpenSenseMapService)
                // history.add(dataPoint.createdAt.)
            }
            // in plots, usually time increases from left to right
            // so we need to reverse the data which has newest first
            history.reverse()

            // TODO probably better to use TimeSeriesPlot?
            val series = SimpleXYSeries(history,
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                    "TODO string for legend (set invisible atm)")
            // TODO use the xml format here?
            val seriesFormat = LineAndPointFormatter(Color.BLACK, Color.BLACK, Color.LTGRAY, null)
            // add the series to the xyplot:
            plot.addSeries(series, seriesFormat)

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            plot.draw(Canvas(bitmap))
            views.setImageViewBitmap(R.id.plot_widget_img, bitmap)

            views.apply {
                setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
            }

            setOnClickPendingIntents(context, appWidgetId, views)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun setOnClickPendingIntents(context: Context, appWidgetId: Int, views: RemoteViews) {
            views.setOnClickPendingIntent(
                    R.id.plot_widget_configuration_button,
                    WidgetHelper.createConfigurationPendingIntent(context, appWidgetId, PlotWidgetConfigurationActivity::class)
            )

            views.setOnClickPendingIntent(
                    R.id.plot_widget_refresh_button,
                    WidgetHelper.createRefreshPendingIntent(context, appWidgetId, PlotWidget::class)
            )
        }
    }
}