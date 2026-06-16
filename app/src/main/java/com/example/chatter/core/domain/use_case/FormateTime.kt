package com.example.chatter.core.domain.use_case


/**
 * 🕐 Shows time like "7:40 PM" if it’s today
 *
 * 📅 Shows "Yesterday" if it’s one day before
 *
 * 📆 Shows day name like "Tuesday" if it’s within this week
 *
 * 📅 Shows full date like "23/07/2024" if it’s older than this week
 */

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FormateTime {

    operator fun invoke(timeStamp: Long): String {
        println("🕐 [FormateTime]  input = $timeStamp")

        if (timeStamp <= 0L) return "Offline"

        val safeMillis = if (timeStamp < 10_000_000_000L) {
            timeStamp * 1000
        } else {
            timeStamp
        }

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = safeMillis }

        // ✅ Normalize both to start of day (fixes midnight bug)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetDay = Calendar.getInstance().apply {
            timeInMillis = safeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffDays =
            ((today.timeInMillis - targetDay.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()

        return when {
            diffDays == 0 -> {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                timeFormat.format(target.time)
            }

            diffDays == 1 -> "Yesterday"

            diffDays in 2..6 -> {
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                dayFormat.format(target.time)
            }

            else -> {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateFormat.format(target.time)
            }
        }
    }
}
