package com.example.chatter.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.R
import com.example.chatter.ui.theme.blue400


@Composable
fun SearchEntriesTxtField(
    modifier: Modifier ,
    hint : String,
    text : String ,
    onTextChange : (String) -> Unit ,
    onClear: () -> Unit = {} ,
    onBack : () -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
        ,
        value = text,
        onValueChange = onTextChange,
        label = {
            if(!isFocused ){ Text(text = hint, fontSize = 18.sp) }
        },
        leadingIcon = {
            if(!isFocused){
                Icon(
                    painter = painterResource(R.drawable.outline_search_24),
                    contentDescription = null
                )
            }else{
                Icon(
                    painter = painterResource(R.drawable.outline_keyboard_arrow_left_24),
                    contentDescription = null ,
                    modifier = Modifier.clickable{
                        onBack()
                    }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent ,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f) ,
            cursorColor = blue400,
            focusedLeadingIconColor = blue400,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        ),
        trailingIcon = {
            if(isFocused){
                Icon(
                    painter = painterResource(R.drawable.outline_cancel_24),
                    contentDescription = null ,
                    modifier = Modifier.clickable{
                        onClear()
                    }
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        textStyle = TextStyle.Default.copy(fontSize = 18.sp),
    )
}
