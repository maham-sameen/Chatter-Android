package com.example.chatter.features.contacts.presentation.contacts.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.core.presentation.Profile
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.features.contacts.presentation.model.Contact
import com.example.chatter.ui.theme.blue400
import com.example.chatter.ui.theme.colorSchemes


@Preview(showBackground = true)
@Composable
fun AContactPreview() {
    val contact = Contact(
        name = "Maham",
        color = colorSchemes.random(),
        phoneNumber = PhoneNumber(
            countryCode = "+92",
            phoneNumber = "3276010123"
        ),
        image = null
    )
    AContact(contact , true , {})
}

@Composable
fun AContact(contact: Contact ,
             isAllowSelection : Boolean  = false,
             onToggleSelection : () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onToggleSelection() }
            .fillMaxWidth()
            .padding(10.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Profile(
            modifier = Modifier.size(50.dp),
            profile = contact.image?.let {
                {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null ,
                        contentScale = ContentScale.Crop ,
                        modifier = Modifier.fillMaxSize(), // ✅ this makes it fill the circle
                    )
                }
            },
            color = contact.color.background,
            firstCapitalLetterTxt = {
                Text(
                    text = contact.name[0].uppercase() ,
                    color = contact.color.icon
                )
            }
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = contact.name
        )
        Spacer(modifier = Modifier.weight(1f))
        if(isAllowSelection){
            Box(modifier = Modifier.size(30.dp)) {
                RadioButton(
                    selected = contact.isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        selectedColor = blue400
                    )
                )
            }
        }
    }
}