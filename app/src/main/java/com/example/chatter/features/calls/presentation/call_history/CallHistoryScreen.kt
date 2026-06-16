package com.example.chatter.features.calls.presentation.call_history

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.auth.presentation.signin.SigninEvents
import com.example.chatter.features.calls.presentation.call_history.compoenent.CallHistoryTopBar
import com.example.chatter.features.calls.presentation.call_history.compoenent.Calls
import com.example.chatter.features.calls.presentation.call_history.compoenent.dummyCallHistory
import com.example.chatter.features.calls.presentation.call_history.model.Call
import kotlinx.coroutines.delay


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun ACallPreview() {
}


@Composable
fun CallHistoryScreen(
    viewModel: CallHistoryViewModel
) {
    val uiState by  viewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val listState = rememberLazyListState() // <-- track scroll
    val paging =  viewModel.calls.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        println("[CallHistoryScreen] LaunchedEffect Refresh Paging")
        paging.refresh()
    }
    Scaffold(
        topBar = {
            CallHistoryTopBar()
        } ,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) {innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Calls(
                listState = listState,
                modifier = Modifier.fillMaxSize(),
                paging = paging
            )
            ShowUIState(
                uiState = uiState,
                onSuccess = {},
                showLoading = { LoadingAnimation() },
                showMessage = {message ->
                    LaunchedEffect(message) {
                        showSnackBarUI(
                            snackBarHostState = snackBarHostState,
                            message = message,
                            actionLabel = "",
                            onDismiss = {},
                            onActionPerformed = {}
                        )
                        delay(1000)
                        viewModel::clearUIState
                    }
                }
            )
        }
    }
}

