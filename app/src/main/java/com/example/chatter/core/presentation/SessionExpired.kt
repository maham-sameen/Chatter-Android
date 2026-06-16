package com.example.chatter.core.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatter.ui.theme.blue400


@Preview
@Composable
fun SessionExpiredDialogPreview() {
    SessionExpiredDialog(
        onDismiss = {},
        onLoginAgain = {}
    )
}


@Composable
fun SessionExpiredDialog(
    onDismiss: () -> Unit,
    onLoginAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(
            text = "Session Expired" ,
            color = MaterialTheme.colorScheme.onSurface
        ) },
        text = { Text(
            "Your session has expired. Please log in again to continue." ,
            color = MaterialTheme.colorScheme.onSurface.copy(0.4f)
        ) },
        confirmButton = {
            Button(
                onClick = onLoginAgain ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue400 ,
                    contentColor = Color.White
                )
            ) {
                Text("Login Again" ,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss ,
            ) {
                Text(
                    "Cancel" ,
                    color = blue400
                )
            }
        }
    )
}
