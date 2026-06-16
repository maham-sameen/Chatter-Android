package com.example.chatter.core.presentation

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.chatter.ui.theme.blue400

@Composable
fun FilledButtonWithBlueBack(
    modifier: Modifier = Modifier ,
    text : String,
    elevation : Dp ,
    onClick : () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors( // ✅ Corrected button colors
            containerColor = blue400,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation)
    ) {
        Text(text, fontSize = 18.sp)
    }
}