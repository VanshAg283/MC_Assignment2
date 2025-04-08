package com.blakester.jetrack.data.database

import androidx.room.TypeConverter
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "flights")
data class Flight(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightCode: String,
    val date: String, // Format: "YYYY-MM-DD"
    val departureTime: String,
    val arrivalTime: String,
    val scheduledDuration: Int, // in minutes
    val actualDuration: Int     // in minutes
)

class ZonedDateTimeConverters {

    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime?): String? {
        return zonedDateTime?.toString()
    }

    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(it) }
    }
}