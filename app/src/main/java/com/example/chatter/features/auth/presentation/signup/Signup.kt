package com.example.chatter.features.auth.presentation.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.R
import com.example.chatter.features.auth.presentation.component.IntroText
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.FilledButtonWithBlueBack
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import com.stevdza_san.library.component.CountryPickerDialog
import com.stevdza_san.library.component.CountryPickerField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun SignupPreview() {
    val viewModel = koinViewModel<SignupViewModel>()
    Signup(viewModel) { }
}




@Composable
fun Signup(
    viewModel: SignupViewModel ,
    onSignUp : (phoneNumber : PhoneNumber) -> Unit
) {
    val showDialog  by  viewModel.dialogState
    val username by viewModel.username
    val password by viewModel.password
    val phoneNumber by viewModel.phoneNumber
    val email by viewModel.email
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) {innerPadding ->
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
                            IntroText(introText = "Sign up with chatter")
                            TxtField(
                                hint = "Enter username",
                                text = username.text,
                                onTextChanged = { viewModel.onEvent(SignupEvents.EnterUsername(it)) },
                                isError = username.isError,
                                isFocused = username.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        SignupEvents.UsernameFocusChanged(
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
                                onTextChanged = { viewModel.onEvent(SignupEvents.EnterPassword(it)) },
                                isError = password.isError,
                                isFocused = password.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        SignupEvents.PasswordFocusChanged(
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
                                hint = "Enter email",
                                text = email.text,
                                onTextChanged = { viewModel.onEvent(SignupEvents.EnterEmail(it)) },
                                isError = email.isError,
                                isFocused = email.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        SignupEvents.EmailFocusChanged(
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
                            Spacer(Modifier.height(10.dp))
                            Column {
                                Row {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_call_24),
                                        contentDescription = "dial phone number",
                                        modifier = Modifier.padding(top = 15.dp, end = 15.dp)
                                    )
                                    AnimatedVisibility(visible = showDialog) {
                                        CountryPickerDialog(
                                            selectedCountry = phoneNumber.country,
                                            onConfirmClick = { country ->
                                                viewModel.onEvent(
                                                    SignupEvents.CountryChanged(
                                                        country
                                                    )
                                                )
                                                viewModel.onEvent(
                                                    SignupEvents.DialogStateChanged(
                                                        false
                                                    )
                                                )
                                            },
                                            onDismiss = {
                                                viewModel.onEvent(
                                                    SignupEvents.DialogStateChanged(
                                                        false
                                                    )
                                                )
                                            }
                                        )
                                    }
                                    Column {
                                        CountryPickerField(
                                            selectedCountry = phoneNumber.country,
                                            onClick = {
                                                viewModel.onEvent(
                                                    SignupEvents.DialogStateChanged(
                                                        true
                                                    )
                                                )
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(5.dp))
                                        TxtField(
                                            fontSize = 15.sp,
                                            hint = "Enter Phone number",
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            text = phoneNumber.phoneNumber.text,
                                            onTextChanged = {
                                                viewModel.onEvent(SignupEvents.EnterPhoneNumber(it))
                                            },
                                            isError = phoneNumber.phoneNumber.isError,
                                            isFocused = phoneNumber.phoneNumber.isFocus,
                                            onFocusChanged = {
                                                viewModel.onEvent(
                                                    SignupEvents.PhoneNumberFocusChanged(
                                                        it
                                                    )
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                imeAction = ImeAction.Done,
                                                keyboardType = KeyboardType.Phone, // 👈 sets phone number keyboard
                                            ),
                                            onDone = true,
                                            onDoneAction = {
                                                println("onDone Action performed in signup")
                                                viewModel.onEvent(SignupEvents.Signup)
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Chatter keeps you connected — fast, private, and easy. Start your conversation now!")
                            Spacer(modifier = Modifier.height(30.dp))
                            FilledButtonWithBlueBack(
                                elevation = 1.dp,
                                onClick = {
                                    viewModel.onEvent(SignupEvents.Signup)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                text = "continue"
                            )
                        }
                    }
                }
            val scope = rememberCoroutineScope()
                ShowUIState(
                    uiState = uiState,
                    onSuccess = {
                        println("\uD83E\uDD2D onSuccess()")
                        onSignUp(
                            PhoneNumber(
                                countryCode = phoneNumber.country.dialCode.toString(),
                                phoneNumber = phoneNumber.phoneNumber.text
                            )
                        )
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
                            viewModel.onEvent(SignupEvents.ClearUIState)
                        }
                    }
                )
            }
    }
}