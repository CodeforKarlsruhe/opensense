package de.codefor.karlsruhe.opensense.widget.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.widget.WidgetHelper

/**
 * The configuration screen for the [AbstractWidget].
 */
abstract class BaseWidgetConfigurationActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private lateinit var boxIdEditText: EditText

    private var addWidgetOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@BaseWidgetConfigurationActivity

        val boxId = boxIdEditText.text.toString()
        if (boxId.isEmpty())
            return@OnClickListener


        WidgetHelper.getSensorList(boxId).subscribe({ result ->
            val sensorIds = mutableListOf<String>()
            result?.forEach({ (id) -> if (id != null) sensorIds.add(id) })

            WidgetHelper.saveConfiguration(context, widgetId, boxId, sensorIds)

            closeConfigurationActivity()
        }, { exception ->
            // TODO: Better error handling
            closeConfigurationActivity()
        })
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.activity_base_widget_configuration)
        boxIdEditText = findViewById<View>(R.id.default_widget_configure_box_id) as EditText
        findViewById<View>(R.id.default_widget_configure_add_button).setOnClickListener(addWidgetOnClickListener)

        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        boxIdEditText.setText(WidgetHelper.loadBoxId(this@BaseWidgetConfigurationActivity, widgetId))
    }

    private fun closeConfigurationActivity() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    protected abstract fun update(context: Context, widgetId: Int, appWidgetManager: AppWidgetManager)
}


