package de.codefor.karlsruhe.opensense.widget.base

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.codefor.karlsruhe.opensense.R
import de.codefor.karlsruhe.opensense.data.boxes.model.Sensor
import kotlinx.android.synthetic.main.recycler_view_sensor_list_item.view.*

class SensorListAdapter(private val sensors: List<Sensor>) : RecyclerView.Adapter<SensorListAdapter.ViewHolder>() {
    private val selectedSensors: MutableList<Sensor> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_sensor_list_item, parent, false)
        return ViewHolder(view, {
            when {
                selectedSensors.contains(it) -> selectedSensors.remove(it)
                else -> selectedSensors.add(it)
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sensors[position])
    }

    override fun getItemCount() = sensors.size

    fun getSelectedItems(): List<Sensor> {
        return selectedSensors
    }


    class ViewHolder(view: View, private val itemClick: (Sensor) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(sensor: Sensor) {
            with(sensor) {
                itemView.sensor_item_title.text = sensor.title
                itemView.sensor_item_value.text = itemView.context.getString(R.string.widget_configuration_current_value,
                        sensor.lastMeasurement?.value, sensor.unit)
                itemView.setOnClickListener({
                    itemView.isSelected = !itemView.isSelected
                    itemClick(this)
                })
            }
        }
    }
}