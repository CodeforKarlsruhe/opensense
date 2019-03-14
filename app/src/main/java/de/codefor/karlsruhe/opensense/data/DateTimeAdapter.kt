package de.codefor.karlsruhe.opensense.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.IOException


class DateTimeAdapter : JsonAdapter<DateTime>() {
    private val fmt = ISODateTimeFormat.dateTime()

    @Synchronized
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): DateTime {
        return DateTime(reader.nextString())
    }

    @Synchronized
    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: DateTime?) {
        writer.value(fmt.print(value))
    }
}