package com.example.chatter.features.auth.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.ui.theme.blue400

@Composable
fun IntroText(
    introText : String
) {
    Spacer(Modifier.height(40.dp))
    Text(
        text = "Chatter",
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        color = blue400
    )
    Spacer(Modifier.height(20.dp))
    Text(
        text = introText ,
        fontSize = 20.sp ,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(40.dp))
}