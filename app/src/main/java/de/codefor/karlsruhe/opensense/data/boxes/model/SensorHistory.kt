package de.codefor.karlsruhe.opensense.data.boxes.model

data class SensorHistory(
        val value: Double?,
        val location: List<String>?,
        // TODO this should be proper timestamp format
        val createdAt: String?
)
