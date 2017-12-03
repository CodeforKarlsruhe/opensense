package de.codefor.karlsruhe.opensense.data.boxes.model

import com.squareup.moshi.Json

data class SensorHistory(
        val value: Double?,
        val location: List<String>?,
        val createdAt: String?
)
