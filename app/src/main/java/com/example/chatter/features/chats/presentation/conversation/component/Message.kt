package com.example.chatter.features.chats.presentation.conversation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatter.features.chats.presentation.conversation.model.Message


@Preview(showBackground = true)
@Composable
fun MessageUIPreview() {
    val message = Message(
        text = "This is the message ",
        formattedTime = "7.40 pm",
        username = "Maham",
        id = "o",
        status = true,
        dateGroup = "Yesterday"
    )
    MessageUI(
        isOwnMessage = false,
        message = message
    )
}


@Composable
fun MessageUI(
    isOwnMessage : Boolean ,
    message : Message
) {
    val bubbleShape = if (isOwnMessage) {
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 4.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = 4.dp,
            bottomEnd = 18.dp
        )
    }

    val bubbleColor = if (isOwnMessage) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val textColor = if (isOwnMessage) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val usernameColor = if (isOwnMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Box(
        contentAlignment = if (isOwnMessage) {
            Alignment.CenterEnd
        } else Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (isOwnMessage) 60.dp else 8.dp,
                end = if (isOwnMessage) 8.dp else 60.dp
            )
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(min = 80.dp, max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.username,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = usernameColor,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.65f)
                    )

                    if (isOwnMessage) {
                        Spacer(modifier = Modifier.width(6.dp))
                        MessageStatusTick(message.status)
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}
