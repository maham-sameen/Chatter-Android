package com.example.chatter.features.auth.presentation.entercode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.R
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.FilledButtonWithBlueBack
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel



@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun EnterCodePreview() {
    val vm = koinViewModel<EnterCodeViewModel>()
    EnterCode(
        {} ,
        viewModel = vm ,
    ){}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterCode(
    goHome: () -> Unit ,
    viewModel : EnterCodeViewModel ,
    phoneNumber : PhoneNumber? = null ,
    username : String? = null ,
    onSuccess : (goHome : Boolean) -> Unit
) {
    val code by  viewModel.code
    val uiState =  viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {goHome()}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_cancel_24),
                            modifier = Modifier
                                .size(25.dp),
                            contentDescription = null ,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {

                }
            )
        }
    ) { innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 40.dp)
            ) {
                    Surface(
                        tonalElevation = 5.dp,
                        shadowElevation = 5.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 40.dp
                                )
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Two-factor \nauthentication",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "To verify your identity enter 6-digits code\n" +
                                        "that we have sent to the associate phone number"
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            TxtField(
                                modifier = Modifier.focusRequester(focusRequester),
                                hint = "6-digits code",
                                text = code.text,
                                onTextChanged = { viewModel.onEvent(EnterCodeEvents.EnterCode(it)) },
                                isError = code.isError,
                                isFocused = code.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        EnterCodeEvents.CodeFocusChanged(
                                            it
                                        )
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number, // 👈 sets phone number keyboard
                                ),
                                onDone = true,
                                onDoneAction = {
                                    viewModel.onEvent(
                                        EnterCodeEvents.Verify(
                                            phoneNumber = phoneNumber,
                                            username = username
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            FilledButtonWithBlueBack(
                                elevation = 1.dp,
                                onClick = {
                                    viewModel.onEvent(
                                        EnterCodeEvents.Verify(
                                            phoneNumber = phoneNumber,
                                            username = username
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                text = "verify"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))
                            Row {
                                Text(text = "Did not get the 6-code verification  ")
                               // Text("Resend", color = Color.Red)
                            }
                        }
                    }
                }
            val scope = rememberCoroutineScope()
                ShowUIState(
                    uiState = uiState.value,
                    onSuccess = {
                        println("\uD83E\uDD2D onSuccess()")
                        scope.launch{
                            showSnackBarUI(
                                snackBarHostState = snackBarHostState,
                                message = "task completed successfully",
                                actionLabel = "",
                                onDismiss = {},
                                onActionPerformed = {}
                            )
                            onSuccess(username != null)
                        }
                    },
                    showLoading = {
                        LoadingAnimation()
                    },
                    showMessage = { message ->
                        LaunchedEffect(message) {
                            showSnackBarUI(
                                snackBarHostState = snackBarHostState,
                                message = message,
                                actionLabel = "",
                                onDismiss = {},
                                onActionPerformed = {}
                            )
                            delay(1000)
                            viewModel.onEvent(EnterCodeEvents.ClearUIState)
                        }
                    }
                )
            }
    }
}