package de.codefor.karlsruhe.opensense.widget.base

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
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

    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var boxInfoLayout: LinearLayout
    private lateinit var boxName: TextView
    private lateinit var boxDescription: TextView
    private lateinit var boxSensorsRecyclerView: RecyclerView

    private lateinit var mapView: MapView


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(Activity.RESULT_CANCELED)

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), getResources().getString(R.string.mapbox_token));

        setContentView(R.layout.activity_base_widget_configuration)

        coordinatorLayout = findViewById<View>(R.id.coordinator_layout) as CoordinatorLayout
        boxInfoLayout = findViewById<View>(R.id.default_widget_configure_box) as LinearLayout
        boxName = findViewById<View>(R.id.default_widget_configure_box_name) as TextView
        boxDescription = findViewById<View>(R.id.default_widget_configure_box_description) as TextView

        boxSensorsRecyclerView = findViewById<View>(R.id.default_widget_configure_box_sensors_recycler_view) as RecyclerView
        boxSensorsRecyclerView.layoutManager = LinearLayoutManager(this)

        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView.onCreate(icicle)

        mapView.getMapAsync { mapboxMap ->
            run {
                // get all boxes from api and display them on the map
                WidgetHelper.getAllBoxes().subscribe({ boxes -> displayBoxesOnMap(mapboxMap, boxes) })

                // handle marker clicks
                mapboxMap.setOnMarkerClickListener({ marker ->
                    run {
                        WidgetHelper.getSenseBox(marker.snippet)
                                .subscribe(this::showBoxInformation) {
                                    Snackbar.make(coordinatorLayout, R.string.widget_configuration_snackbar_error_loading, Snackbar.LENGTH_SHORT)
                                            .show()
                                }

                        return@setOnMarkerClickListener true
                    }
                })

            }
        }

        val extras = intent.extras
        if (extras != null) {
            widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        when (widgetId) {
            AppWidgetManager.INVALID_APPWIDGET_ID -> finish()
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

    private fun displayBoxesOnMap(mapboxMap: MapboxMap, boxes: List<SenseBox>) {
        if (boxes.isEmpty()) {
            Snackbar.make(coordinatorLayout, R.string.widget_configuration_snackbar_error_loading, Snackbar.LENGTH_SHORT)
                    .show()
            return
        }

        for (box in boxes) {
            val coordinates = box.loc?.get(0)?.geometry?.coordinates ?: continue
            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(coordinates[1], coordinates[0]))
                    .title(box.name)
                    .snippet(box.id)
            )
        }
    }

    private fun showBoxInformation(senseBox: SenseBox) {
        if (senseBox.id == null || senseBox.sensors == null) {
            Snackbar.make(coordinatorLayout, R.string.widget_configuration_snackbar_error_invalid_data, Snackbar.LENGTH_SHORT)
                    .show()
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
            if (adapter.getSelectedItems().isEmpty()) {
                Snackbar.make(coordinatorLayout, R.string.widget_configuration_snackbar_empty_list, Snackbar.LENGTH_SHORT)
                        .show()
            } else if (adapter.getSelectedItems().size <= maxSensorItems) {
                WidgetHelper.saveConfiguration(this, widgetId, boxId, adapter.getSelectedItems())
                update(widgetId)
                closeConfigurationActivity()
            } else {
                val text = resources.getQuantityString(R.plurals.maximumNumberOfSensors, maxSensorItems, maxSensorItems)
                Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
                        .show()
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


