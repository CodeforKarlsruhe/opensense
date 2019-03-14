package de.codefor.karlsruhe.opensense.data.boxes.model

import com.squareup.moshi.Json

data class Sensor(
        @Json(name = "_id") val id: String?,
        val lastMeasurement: LastMeasurement?,
        val sensorType: String?,
        val title: String?,
        val unit: String?
)
