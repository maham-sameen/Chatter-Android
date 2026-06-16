package com.example.chatter.features.chats.presentation.conversation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageStatusTick(status: Boolean) {
    val labelColor = MaterialTheme.colorScheme.onPrimaryContainer
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (status) {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Seen",
                style = MaterialTheme.typography.labelSmall,
                color = labelColor.copy(alpha = 0.7f)
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .border(1.dp, labelColor.copy(alpha = 0.4f), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Sent",
                style = MaterialTheme.typography.labelSmall,
                color = labelColor.copy(alpha = 0.55f)
            )
        }
    }
}
