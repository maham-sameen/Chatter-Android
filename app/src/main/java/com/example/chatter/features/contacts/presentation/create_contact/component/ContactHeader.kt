package com.example.chatter.features.contacts.presentation.create_contact.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ContactHeader(
    contactNameTxtField : @Composable () -> Unit ,
) {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth() ,
        horizontalAlignment = Alignment.CenterHorizontally ,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Create New Contact" , fontSize = 20.sp , fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        contactNameTxtField()
        Spacer(Modifier.height(40.dp))
    }
}

