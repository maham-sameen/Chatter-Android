package com.example.chatter.features.chats.presentation.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.R
import com.example.chatter.core.presentation.topAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAppBar(
    onBackPressed : () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Profile" ,
                modifier = Modifier.padding(start = 10.dp) ,
                fontSize = 22.sp ,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = Modifier,
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_keyboard_arrow_left_24) ,
                contentDescription = null ,
                modifier = Modifier
                    .clickable{
                        onBackPressed()
                    }
            )
        },
        actions = {},
        colors = topAppBarColors(),
    )
}