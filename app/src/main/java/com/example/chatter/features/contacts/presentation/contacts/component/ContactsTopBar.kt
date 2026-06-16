package com.example.chatter.features.contacts.presentation.contacts.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.chatter.core.presentation.topAppBarColors


@Preview(showBackground = true)
@Composable
fun ContactsTopBarPreview() {
    ContactsTopBar()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsTopBar() {
    TopAppBar(
        title = {
            Text(
                "Contacts" ,
                fontSize = 23.sp ,
                fontWeight = FontWeight.Bold ,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier,
        navigationIcon = {},
        actions = {},
        colors = topAppBarColors(),
    )
}