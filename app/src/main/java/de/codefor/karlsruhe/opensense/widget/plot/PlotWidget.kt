package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import android.view.View
import com.androidplot.util.PixelUtils
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
                }, {
                    views.apply {
                            // Show refresh button, hide progress bar
                            setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                            setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                            // Remove values, set error text
                            // TODO a resource error string on it's own for plotwidget?
                            setTextViewText(R.id.plot_widget_error_text, context.getString(R.string.one_value_error_text))
                            setViewVisibility(R.id.plot_widget_error_text, View.VISIBLE)
                            setViewVisibility(R.id.plot_widget_img, View.GONE)
                    }
                    setOnClickPendingIntents(context, appWidgetId, views)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            )
        }

        private fun drawPlot(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager, sensorHist: List<SensorHistory>) {
            val views = RemoteViews(context.getPackageName(), R.layout.plot_widget)

            val plot = XYPlot(context, "Historyplot")

            // TODO show and set x- and y-tics
            // TODO use proper strings depending on selected sensor
            plot.setRangeLabel("Temp. in Grad")
            plot.setDomainLabel("Zeit")

            // show the tic labels
            plot.graph.setLineLabelEdges(XYGraphWidget.Edge.RIGHT, XYGraphWidget.Edge.BOTTOM)
            // move the tic labels
            plot.graph.lineLabelInsets.right = PixelUtils.dpToPix(20f)
            plot.graph.lineLabelInsets.bottom = PixelUtils.dpToPix(4f)
            // format the tic labels
            plot.graph.getLineLabelStyle(XYGraphWidget.Edge.RIGHT).paint.color = Color.BLACK
            plot.graph.getLineLabelStyle(XYGraphWidget.Edge.RIGHT).paint.textSize = 25f
            plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.color = Color.BLACK
            plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.textSize = 25f

            plot.title.labelPaint.textSize = 25f
            plot.rangeTitle.labelPaint.textSize = 25f
            plot.domainTitle.labelPaint.textSize = 25f

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
            val seriesFormat = LineAndPointFormatter(Color.BLACK, Color.BLACK, Color.TRANSPARENT, null)
            // add the series to the xyplot:
            plot.addSeries(series, seriesFormat)

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            plot.draw(Canvas(bitmap))
            views.setImageViewBitmap(R.id.plot_widget_img, bitmap)

            views.apply {
                setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                setViewVisibility(R.id.plot_widget_error_text, View.GONE)
                setViewVisibility(R.id.plot_widget_img, View.VISIBLE)
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
