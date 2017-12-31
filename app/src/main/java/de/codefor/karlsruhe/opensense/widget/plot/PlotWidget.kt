package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.androidplot.ui.HorizontalPositioning
import com.androidplot.ui.VerticalPositioning
import com.androidplot.util.PixelUtils
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget

class PlotWidget : BaseWidget() {

    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        onUpdateWidget(context, appWidgetId, appWidgetManager)
    }

    companion object {
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
                    views.apply {
                        //Show refresh button, hide progress bar
                        setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                        setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                        // TODO: Return box in above call and set box name and sensor name
                        setTextViewText(R.id.plot_widget_box_name, "sensebox name")
                        setTextViewText(R.id.plot_widget_sensor_title, "sensor")
                    }
                    drawPlot(context, appWidgetId, appWidgetManager, sensorHist)
                }, {
                    views.apply {
                            // Show refresh button, hide progress bar
                            setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                            setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                            // Remove values
                            setViewVisibility(R.id.plot_widget_error_text, View.VISIBLE)
                            setViewVisibility(R.id.plot_widget_img, View.GONE)
                    }
                    setOnClickPendingIntents(context, appWidgetId, views)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            )
        }

        private fun drawPlot(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager, sensorHist: List<SensorHistory>) {
            val views = RemoteViews(context.packageName, R.layout.plot_widget)

            val plot = XYPlot(context, context.getString(R.string.plot_history_title))

            // TODO use proper strings depending on selected sensor
            plot.setRangeLabel("Grad")
            plot.setDomainLabel("Zeit")

            val textSize = 20f

            // Configure the graph
            plot.graph.apply {
                // show the tic labels
                setLineLabelEdges(XYGraphWidget.Edge.RIGHT, XYGraphWidget.Edge.BOTTOM)
                // add space for the labels
                paddingRight = 60f
                paddingBottom = 30f
                // move the tic labels outside (negative)
                lineLabelInsets.right = PixelUtils.dpToPix(-20f)
                lineLabelInsets.bottom = PixelUtils.dpToPix(-20f)
                // format the tic labels
                getLineLabelStyle(XYGraphWidget.Edge.RIGHT).paint.color = Color.WHITE
                getLineLabelStyle(XYGraphWidget.Edge.RIGHT).paint.textSize = textSize
                getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.color = Color.WHITE
                getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.textSize = textSize
            }

            // Configure the labels and background
            plot.title.labelPaint.textSize = textSize
            plot.rangeTitle.labelPaint.textSize = textSize
            plot.rangeTitle.position(
                    20f, HorizontalPositioning.ABSOLUTE_FROM_RIGHT,
            70f, VerticalPositioning.ABSOLUTE_FROM_BOTTOM)
            plot.domainTitle.labelPaint.textSize = textSize
            plot.legend.isVisible = false
            plot.setBackgroundColor(Color.TRANSPARENT)

            val h = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            val w = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            plot.setPlotMargins(10f, 10f, 10f, 10f)

            plot.layout(0, 0, w, h)

            Log.i("PlotWidget", "sensorHistory size: ${sensorHist.size}, data: $sensorHist")

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
