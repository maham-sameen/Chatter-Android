package com.example.chatter.features.contacts.presentation.create_chat.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.R
import com.example.chatter.core.presentation.topAppBarColors

@Preview(showBackground = true)
@Composable
fun GroupTopBarPreview() {
    CreateChatTopBar()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "New Group" ,
                fontSize = 20.sp ,
                fontWeight = FontWeight.Bold ,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier,
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_keyboard_arrow_left_24) ,
                contentDescription = null ,
                modifier = Modifier
                    .size(30.dp) ,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {},
        colors = topAppBarColors(),
    )
}