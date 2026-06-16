package com.example.chatter.features.chats.presentation.conversation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview(showBackground = true)
@Composable
fun DateGroupPreview() {
    DateGroupHeader("Yesterday")
}


@Composable
fun DateGroup(date : String) {
    Surface(
        shape = RoundedCornerShape(4.dp) ,
        color = MaterialTheme.colorScheme.surface ,
        tonalElevation = 4.dp ,
        shadowElevation = 4.dp ,
    ){
        Text(
            text = date ,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface ,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp)
        )
    }
}

@Composable
fun DateGroupHeader(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
