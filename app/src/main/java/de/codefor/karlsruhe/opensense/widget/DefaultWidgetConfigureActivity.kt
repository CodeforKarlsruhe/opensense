package de.codefor.karlsruhe.opensense.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import de.codefor.karlsruhe.opensense.R

/**
 * The configuration screen for the [DefaultWidget].
 */
class DefaultWidgetConfigureActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private lateinit var boxIdEditText: EditText

    private var addWidgetOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@DefaultWidgetConfigureActivity

        saveBoxId(context, widgetId, boxIdEditText.text.toString())


        val appWidgetManager = AppWidgetManager.getInstance(context)
        DefaultWidget.updateAppWidget(context, appWidgetManager, widgetId)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.default_widget_configure)
        boxIdEditText = findViewById<View>(R.id.default_widget_box_id) as EditText
        findViewById<View>(R.id.default_widget_add_button).setOnClickListener(addWidgetOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        boxIdEditText.setText(loadBoxId(this@DefaultWidgetConfigureActivity, widgetId))
    }

    companion object {
        private val PREFS_NAME = "de.codefor.karlsruhe.opensense.widget.DefaultWidget"
        private val PREF_BOX_ID = "box_id_"

        internal fun saveBoxId(context: Context, appWidgetId: Int, boxId: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_BOX_ID + appWidgetId, boxId)
            prefs.apply()
        }

        internal fun loadBoxId(context: Context, appWidgetId: Int): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val boxId = prefs.getString(PREF_BOX_ID + appWidgetId, null)
            return boxId ?: ""
        }

        internal fun deleteBoxId(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_BOX_ID + appWidgetId)
            prefs.apply()
        }
    }
}

