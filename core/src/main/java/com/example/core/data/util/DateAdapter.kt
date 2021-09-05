package com.example.core.data.util

import com.squareup.moshi.*
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter: JsonAdapter<Date>() {
    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        var value = reader.nextString()
        value = if(!value.contains(":")) "$value 00:00:00" else value

        val valuePattern = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'"

        val format = SimpleDateFormat(valuePattern, Locale("in"))
        return try {
           val date =  format.parse(value)
            date
        }catch (ex: ParseException){
            Timber.e(ex)
            Date(0)
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        writer.value(SimpleDateFormat("yyyy'-'MM'-'dd HH':'mm':'ss'Z'", Locale("in")).format(value))
    }

}