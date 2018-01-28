package de.codefor.karlsruhe.opensense.data.boxes.model

import org.joda.time.DateTime

data class LastMeasurement(
		val createdAt: DateTime?,
		val value: String?
)
