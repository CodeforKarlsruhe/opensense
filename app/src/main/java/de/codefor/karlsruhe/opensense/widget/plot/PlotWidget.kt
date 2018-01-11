package de.codefor.karlsruhe.opensense.widget.plot

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.androidplot.ui.HorizontalPositioning
import com.androidplot.ui.VerticalPositioning
import com.androidplot.util.PixelUtils
import com.androidplot.xy.*
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import de.codefor.karlsruhe.opensense.data.boxes.model.SensorHistory
import de.codefor.karlsruhe.opensense.widget.WidgetHelper
import de.codefor.karlsruhe.opensense.widget.base.BaseWidget
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition


class PlotWidget : BaseWidget() {

    override fun onUpdateWidget(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
        update(context, appWidgetId, appWidgetManager)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager,
                                           appWidgetId: Int, newOptions: Bundle) {
        onUpdateWidget(context, appWidgetId, appWidgetManager)
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormat.forPattern("d. M., HH:mm")
        // Use a different formatter without full the date for the first and last tick
        private val dateTimeFormatterEdges = DateTimeFormat.forPattern("HH:mm")


        fun update(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
            val views = RemoteViews(context.packageName, R.layout.plot_widget)

            //Show progress bar, hide refresh button
            views.apply {
                setViewVisibility(R.id.plot_widget_refresh_button, View.INVISIBLE)
                setViewVisibility(R.id.plot_widget_progress_bar, View.VISIBLE)
                setProgressBar(R.id.plot_widget_progress_bar, 100, 0, true)
            }
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)

            WidgetHelper.getSenseBoxAndSensorData(context, appWidgetId).subscribe(
                // onSuccess
                { (senseBox, sensorData) ->
                    // Kotlin doesn't support nested destructuring, so we do it here
                    val (sensor, sensorHist) = sensorData
                    views.apply {
                        //Show refresh button, hide progress bar
                        setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                        setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                        setTextViewText(R.id.plot_widget_box_name, senseBox.name)
                        setTextViewText(R.id.plot_widget_sensor_title, sensor.title)
                    }
                    appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views)
                    drawPlot(context, appWidgetId, appWidgetManager, senseBox, sensor, sensorHist)
                }, {
                    showErrorScreen(context, appWidgetId, appWidgetManager, views)
                }
            )
        }

        private fun showErrorScreen(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager,
                                    views: RemoteViews) {
            views.apply {
                // Show refresh button, hide progress bar
                setViewVisibility(R.id.plot_widget_refresh_button, View.VISIBLE)
                setViewVisibility(R.id.plot_widget_progress_bar, View.GONE)
                // Remove values
                setViewVisibility(R.id.plot_widget_error_text, View.VISIBLE)
                //setViewVisibility(R.id.plot_widget_img, View.GONE)
            }
            setOnClickPendingIntents(context, appWidgetId, views)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun drawPlot(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager,
                             senseBox: SenseBox, sensor: Sensor, sensorHist: List<SensorHistory>) {
            val views = RemoteViews(context.packageName, R.layout.plot_widget)

            val dates = mutableListOf<DateTime>()
            val values = mutableListOf<Double>()

            // The time in a plot usually increases from left to right.
            // The provided data starts with the newest first and we have to reverse it.
            sensorHist.reversed().forEach { (value, _, createdAt) ->
                dates.add(createdAt ?: DateTime.now())
                values.add(value ?: Double.NaN)
            }

            if (dates.isEmpty() || values.isEmpty()) {
                showErrorScreen(context, appWidgetId, appWidgetManager, views)
                return
            }

            val plot = XYPlot(context, "") // no title for the plot, it should be self-evident
            plot.apply {
                setRangeLabel(sensor.unit)
                setRangeStep(StepMode.SUBDIVIDE, 4.0)
                setDomainLabel(context.getString(R.string.plot_graph_time))
                setDomainStep(StepMode.SUBDIVIDE, 3.0)
                setBackgroundColor(Color.TRANSPARENT)
            }


            val textSize = PixelUtils.spToPix(7f)

            // Configure the graph
            plot.graph.apply {
                // show the tick labels
                setLineLabelEdges(XYGraphWidget.Edge.LEFT, XYGraphWidget.Edge.BOTTOM)

                // add space for the labels
                marginLeft = PixelUtils.dpToPix(24f)
                marginTop = PixelUtils.dpToPix(8f)
                marginRight = PixelUtils.dpToPix(12f)
                marginBottom = PixelUtils.dpToPix(16f)

                lineLabelInsets.left = PixelUtils.dpToPix(-15f)
                lineLabelInsets.bottom = PixelUtils.dpToPix(-8f)

                // format the labels
                getLineLabelStyle(XYGraphWidget.Edge.LEFT).paint.color = Color.WHITE
                getLineLabelStyle(XYGraphWidget.Edge.LEFT).paint.textSize = textSize
                getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.color = Color.WHITE
                getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.textSize = textSize

                // format the DateTime
                getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
                    override fun format(obj: Any, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                        val index = (obj as Number).toInt()
                        if (index < 0 || index >= dates.size) return toAppendTo

                        val date = dates[index]

                        return when (index) {
                            0, dates.lastIndex -> toAppendTo.append(date.toString(dateTimeFormatterEdges))
                            else -> toAppendTo.append(date.toString(dateTimeFormatter))
                        }
                    }

                    override fun parseObject(source: String, pos: ParsePosition): Any? {
                        return null
                    }
                }
            }

            // Format the title (time and unit)
            plot.rangeTitle.labelPaint.textSize = textSize
            plot.rangeTitle.position(
                    25f, HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                    -10f, VerticalPositioning.ABSOLUTE_FROM_CENTER)
            plot.domainTitle.labelPaint.textSize = textSize
            plot.domainTitle.position(
                    0f, HorizontalPositioning.ABSOLUTE_FROM_CENTER,
                    25f, VerticalPositioning.ABSOLUTE_FROM_BOTTOM)
            plot.legend.isVisible = false

            val h = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            val w = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            plot.layout(0, 0, w, h)

            val series = SimpleXYSeries(values, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "")
            val seriesFormat = LineAndPointFormatter(Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT, null)

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
