package com.example.chatter.core.presentation

import androidx.compose.runtime.Composable
import com.example.chatter.core.presentation.model.UIState

@Composable
fun ShowUIState(
    uiState: UIState,
    onSuccess: () -> Unit,
    showLoading: @Composable () -> Unit,
    showMessage: @Composable (message: String) -> Unit
) {
    when(uiState){
        is UIState.Loading -> showLoading()
        is UIState.Success -> onSuccess()
        is UIState.Error -> showMessage(uiState.message)
        is UIState.Idle -> return
    }
}