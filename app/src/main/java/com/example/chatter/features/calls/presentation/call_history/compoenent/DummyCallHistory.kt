package com.example.chatter.features.calls.presentation.call_history.compoenent


import androidx.compose.ui.graphics.Color
import com.example.chatter.R
import com.example.chatter.features.calls.presentation.call_history.model.Call
import com.example.chatter.ui.ColorScheme
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.*

val dummyCallHistory: List<Call> = List(30) { index ->
    Call(
        callId = UUID.randomUUID().toString(),
        members = List(Random.nextInt(1, 4)) { "+92-${Random.nextInt(300000000, 399999999)}" },
        colorScheme = ColorScheme(
            background = Color(
                red = Random.nextInt(50, 256),
                green = Random.nextInt(50, 256),
                blue = Random.nextInt(50, 256)
            ),
            icon = Color.White
        ),
        name = "Contact ${index + 1}",
        status = if (Random.nextBoolean()) "Incoming" else "Outgoing",
        isVideo = Random.nextBoolean(),
        timeStamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
            Date(System.currentTimeMillis() - Random.nextLong(0, 7 * 24 * 60 * 60 * 1000))
        ),
        image = null,
        statusIcon = R.drawable.outline_keyboard_arrow_left_24
    )
}
