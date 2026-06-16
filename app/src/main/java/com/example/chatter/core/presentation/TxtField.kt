package com.example.chatter.core.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.ui.theme.blue400

@Composable
fun TxtField(
    modifier: Modifier  = Modifier,
    fontSize : TextUnit =  15.sp,
    hint : String,
    containerColor : Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.2f),
    trailingIcon : (@Composable () -> Unit)? = null,
    enabled : Boolean = true,
    text : String = "",
    onTextChanged : (String) -> Unit = {},
    isError : Boolean ,
    isFocused : Boolean,
    onFocusChanged : (FocusState) -> Unit = {},
    singleLine : Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next
    ),
    onDone : Boolean = false ,
    onDoneAction : () -> Unit = {} ,
    leadingIcon : (@Composable () -> Unit)? = null
) {
    println("\uD83D\uDE08 ContactTxtField text $text , isError $isError , isFocused $isFocused")
    val focusManager  = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        modifier = Modifier
            .heightIn(min = 60.dp)
            .then(modifier)
        .fillMaxWidth()
        //.padding(end = 10.dp)
        .onFocusChanged { onFocusChanged(it) }
        ,
        value =text,
        enabled = enabled,
        onValueChange = onTextChanged,
        label = {
            if(!isFocused && text.isEmpty()){
                Text(text = hint , fontSize = fontSize)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = containerColor,
            focusedContainerColor =containerColor ,
            cursorColor = blue400 ,
            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledContainerColor = containerColor ,
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) ,
            focusedTextColor = MaterialTheme.colorScheme.onSurface ,
            disabledTextColor = MaterialTheme.colorScheme.onSurface ,
            errorBorderColor = Color.Red.copy(0.5f)
        ),
        textStyle = TextStyle.Default.copy(fontSize = fontSize),
        trailingIcon = trailingIcon,
        isError = isError ,
        singleLine = singleLine ,
        keyboardActions =  if(!onDone) {
            KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)

                }
            )
        }else {
            KeyboardActions(
                onDone = {
                    println("onDone of the text field ")
                    focusManager.clearFocus()
                    keyboardController?.hide()  // 👈 hide the keyboard
                    onDoneAction()
                }
            )
        },
        keyboardOptions = keyboardOptions ,
        leadingIcon = leadingIcon
    )
}

//https://stackoverflow.com/questions/76228381/how-to-hide-the-keyboard-in-dialog-with-compose