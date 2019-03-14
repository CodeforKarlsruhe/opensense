package de.codefor.karlsruhe.opensense.data.boxes.model

import com.squareup.moshi.Json
import org.joda.time.DateTime

data class SenseBox(
        @Json(name = "_id") val id: String?,
        val createdAt: DateTime?,
        val updatedAt: DateTime?,
        val name: String?,
        val boxType: String?,
        val model: String?,
        val grouptag: String?,
        val exposure: String?,
        val weblink: String?,
        val description: String?,
        val currentLocation: Geometry?,
        val loc: List<LocItem>?,
        val sensors: List<Sensor>?
)
