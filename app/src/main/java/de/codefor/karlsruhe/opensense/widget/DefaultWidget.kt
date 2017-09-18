package de.codefor.karlsruhe.opensense.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.data.OpenSenseMapService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Implementation of default widget containing a maximum of five sensor data.
 * The configuration is implemented in [DefaultWidgetConfigureActivity].
 */
class DefaultWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            DefaultWidgetConfigureActivity.deleteBoxId(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            val boxId = DefaultWidgetConfigureActivity.loadBoxId(context, appWidgetId)
            if (boxId.isEmpty()) return

            OpenSenseMapService.getBox(boxId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        val views = RemoteViews(context.packageName, R.layout.default_widget)
                        views.setTextViewText(R.id.default_widget_text, result.name)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    })
        }
    }
}

