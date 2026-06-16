package com.example.chatter.core.presentation.model

data class FocusedErrorTxtFieldState(
    val isFocus : Boolean  = false,
    val text : String = "" ,
    val isError : Boolean = false
)