package com.example.chatter.features.chats.domain.use_case.conversation


import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FormateMessageTimeStamp {

    @RequiresApi(Build.VERSION_CODES.O)
    operator  fun invoke(timestamp: Long): Pair<String, String> {
        val zone = ZoneId.systemDefault()
        val dateTime = Instant.ofEpochMilli(timestamp).atZone(zone)
        val date = dateTime.toLocalDate()
        val now = LocalDate.now(zone)

        // Time part
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val timePart = dateTime.format(timeFormatter)

        // Date part
        val datePart = when {
            date.isEqual(now) -> "Today"
            date.isEqual(now.minusDays(1)) -> "Yesterday"
            date.isAfter(now.minusDays(now.dayOfWeek.value.toLong())) -> {
                // If within this week
                date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() } // "Tuesday"
            }
            else -> {
                val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM") // "Tuesday, 12 Nov"
                date.format(formatter)
            }
        }

        return Pair(datePart, timePart)
    }
}

