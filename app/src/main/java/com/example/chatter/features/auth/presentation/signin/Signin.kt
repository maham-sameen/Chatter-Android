package com.example.chatter.features.auth.presentation.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.features.auth.presentation.component.IntroText
import com.example.chatter.core.presentation.FilledButtonWithBlueBack
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import kotlinx.coroutines.delay


@Composable
fun Signin(
    viewModel: SigninViewModel ,
    onSignIn : (username : String) -> Unit
) {
    val username by viewModel.username
    val password by viewModel.password
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState)
    },){ innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
                            IntroText(introText = "log in with chatter")
                            TxtField(
                                modifier = Modifier.focusRequester(focusRequester),
                                hint = "Enter username",
                                text = username.text,
                                onTextChanged = { viewModel.onEvent(SigninEvents.EnterUsername(it)) },
                                isError = username.isError,
                                isFocused = username.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        SigninEvents.UsernameFocusChanged(
                                            it
                                        )
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Text, // 👈 sets phone number keyboard
                                )
                            )
                            Spacer(Modifier.height(5.dp))
                            TxtField(
                                hint = "Enter password",
                                text = password.text,
                                onTextChanged = { viewModel.onEvent(SigninEvents.EnterPassword(it)) },
                                isError = password.isError,
                                isFocused = password.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        SigninEvents.PasswordFocusChanged(
                                            it
                                        )
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Text, // 👈 sets phone number keyboard
                                ),
                                onDone = true,
                                onDoneAction = {
                                    viewModel.onEvent(SigninEvents.Signin)
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Chatter keeps you connected — fast, private, and easy. Start your conversation now!")
                            Spacer(modifier = Modifier.height(30.dp))
                            FilledButtonWithBlueBack(
                                elevation = 1.dp,
                                onClick = { viewModel.onEvent(SigninEvents.Signin) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                text = "continue"
                            )
                        }
                    }
                    ShowUIState(
                        uiState = uiState,
                        onSuccess = {
                            println("\uD83E\uDD2D onSuccess()")
                            onSignIn(username.text)
                        },
                        showLoading = { LoadingAnimation() },
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
                                viewModel.onEvent(SigninEvents.ClearUIState)
                            }
                        }
                    )
                }
            }
    }
}