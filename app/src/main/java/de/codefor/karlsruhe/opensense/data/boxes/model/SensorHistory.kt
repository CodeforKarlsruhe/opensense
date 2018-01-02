package de.codefor.karlsruhe.opensense.data.boxes.model

import org.joda.time.DateTime

data class SensorHistory(
        val value: Double?,
        val location: List<String>?,
        val createdAt: DateTime?
)
