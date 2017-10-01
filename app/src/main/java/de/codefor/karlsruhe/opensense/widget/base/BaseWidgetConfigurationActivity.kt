package de.codefor.karlsruhe.opensense.widget.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.data.boxes.model.SenseBox
import de.codefor.karlsruhe.opensense.widget.WidgetHelper


/**
 * The configuration screen for the widgets.
 */
abstract class BaseWidgetConfigurationActivity : AppCompatActivity() {
    // The maximum items that can be selected. Set in the child class.
    protected var maxSensorItems = 0

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var boxId = ""

    private lateinit var boxIdEditText: EditText
    private lateinit var boxInfoLayout: LinearLayout
    private lateinit var boxName: TextView
    private lateinit var boxDescription: TextView
    private lateinit var boxSensorsRecyclerView: RecyclerView

    private var addWidgetOnClickListener: View.OnClickListener = View.OnClickListener {
        // TODO: Better error handling
        WidgetHelper.getSenseBox(boxIdEditText.text.toString())
                .subscribe(this::showBoxInformation) { finish() }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.activity_base_widget_configuration)

        findViewById<View>(R.id.default_widget_configure_select).setOnClickListener(addWidgetOnClickListener)
        boxIdEditText = findViewById<View>(R.id.default_widget_configure_id) as EditText

        boxInfoLayout = findViewById<View>(R.id.default_widget_configure_box) as LinearLayout
        boxName = findViewById<View>(R.id.default_widget_configure_box_name) as TextView
        boxDescription = findViewById<View>(R.id.default_widget_configure_box_description) as TextView

        boxSensorsRecyclerView = findViewById<View>(R.id.default_widget_configure_box_sensors_recycler_view) as RecyclerView
        boxSensorsRecyclerView.layoutManager = LinearLayoutManager(this)
        boxSensorsRecyclerView.adapter

        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        when (widgetId) {
            AppWidgetManager.INVALID_APPWIDGET_ID -> finish()
            else -> boxIdEditText.setText(WidgetHelper.loadBoxId(this@BaseWidgetConfigurationActivity, widgetId))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.base_widget_configuration, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            saveAndShowWidget()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBoxInformation(senseBox: SenseBox) {
        if (senseBox.id == null || senseBox.sensors == null) {
            // TODO: Better error handling
            finish()
            return
        }

        boxId = senseBox.id
        boxInfoLayout.visibility = View.VISIBLE
        boxName.text = senseBox.name
        boxDescription.text = senseBox.description
        boxSensorsRecyclerView.adapter = SensorListAdapter(senseBox.sensors)
    }

    private fun saveAndShowWidget() {
        val adapter = boxSensorsRecyclerView.adapter
        if (adapter != null && adapter is SensorListAdapter) {
            // TODO: Show snackbar when to much items are selected
            if (adapter.getSelectedItems().isNotEmpty() && adapter.getSelectedItems().size <= maxSensorItems) {
                WidgetHelper.saveConfiguration(this, widgetId, boxId, adapter.getSelectedItems())
                update(widgetId)
                closeConfigurationActivity()
            }
        }
    }

    private fun closeConfigurationActivity() {
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    protected abstract fun update(widgetId: Int)
}


