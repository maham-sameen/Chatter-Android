package com.example.chatter.core.presentation.model

sealed class UIState {
    object Loading : UIState()
    object Success : UIState()
    data class Error(val message: String) : UIState()
    object Idle : UIState()
}

