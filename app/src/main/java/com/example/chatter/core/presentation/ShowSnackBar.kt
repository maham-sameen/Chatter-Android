package com.example.chatter.core.presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult


suspend fun showSnackBarUI(
    snackBarHostState: SnackbarHostState ,
    message : String ,
    actionLabel : String? = null ,
    onDismiss : () -> Unit = {} ,
    onActionPerformed : () -> Unit = {}
){
    val snackBarResult = snackBarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = SnackbarDuration.Short
    )
    when (snackBarResult) {
        SnackbarResult.Dismissed -> onDismiss
        SnackbarResult.ActionPerformed -> onActionPerformed
    }
}

