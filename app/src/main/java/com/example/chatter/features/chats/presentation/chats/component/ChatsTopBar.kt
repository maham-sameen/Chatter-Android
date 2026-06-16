package com.example.chatter.features.chats.presentation.chats.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun ChatsTopBar(
    goSetting: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Chatter" ,
                fontSize = 30.sp ,
                fontWeight = FontWeight.Bold ,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier,
        navigationIcon = {},
        actions = {
            Icon(
                painter = painterResource(R.drawable.outline_settings_24) ,
                contentDescription = null ,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(30.dp)
                    .clickable {
                        goSetting()
                    }
                ,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    )
}
