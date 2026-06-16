package com.example.chatter.features.contacts.presentation.create_contact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.contacts.presentation.create_contact.component.ContactBar
import com.example.chatter.features.contacts.presentation.create_contact.component.ContactHeader
import com.stevdza_san.library.component.CountryPickerDialog
import com.stevdza_san.library.component.CountryPickerField
import kotlinx.coroutines.delay
import org.koin.core.annotation.KoinExperimentalAPI


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun CreateContactScreenPreview() {}


@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateContactScreen(
    viewModel: CreateContactViewModel ,
    onSave : () -> Unit
) {
    val nameTxtFieldState = viewModel.name
    val phoneNoTxtFieldState = viewModel.phoneNumber
    val saveAndContinue = viewModel.isSaveAndContinue
    var showDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    Scaffold (
        topBar = {
            ContactBar(
                isSaveContinue = saveAndContinue.value,
                onBackPressed = {},
                onSaveAndContinue = {
                    viewModel.onEvent(CreateContactEvent.SaveAndContinue)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(
                    modifier = Modifier.padding(top = 20.dp, end = 20.dp)
                ) {
                    ContactHeader(
                        contactNameTxtField = {
                            TxtField(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .padding(start = 60.dp, end = 50.dp),
                                fontSize = 20.sp,
                                hint = "Enter contact name",
                                containerColor = MaterialTheme.colorScheme.surface,
                                text = nameTxtFieldState.value.text,
                                onTextChanged = {
                                    viewModel.onEvent(CreateContactEvent.EnterName(it))
                                },
                                isError = nameTxtFieldState.value.isError,
                                isFocused = nameTxtFieldState.value.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(CreateContactEvent.NameFocusChange(it))
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                )
                            )
                        })
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 20.dp)
                ) {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.outline_call_24),
                            contentDescription = "dial phone number",
                            modifier = Modifier.padding(top = 15.dp, end = 15.dp)
                        )

                        AnimatedVisibility(visible = showDialog) {
                            CountryPickerDialog(
                                selectedCountry = phoneNoTxtFieldState.value.country,
                                onConfirmClick = { country ->
                                    viewModel.onEvent(
                                        CreateContactEvent.ChangeCountry(country)
                                    )
                                    showDialog = false
                                },
                                onDismiss = { showDialog = false }
                            )
                        }
                        Column {
                            CountryPickerField(
                                selectedCountry = phoneNoTxtFieldState.value.country,
                                onClick = { showDialog = true }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            TxtField(
                                modifier = Modifier.padding(end = 50.dp),
                                fontSize = 15.sp,
                                hint = "Enter Phone number",
                                containerColor = MaterialTheme.colorScheme.surface,
                                text = phoneNoTxtFieldState.value.phoneNumber.text,
                                onTextChanged = {
                                    viewModel.onEvent(CreateContactEvent.EnterPhoneNo(it))
                                },
                                isError = phoneNoTxtFieldState.value.phoneNumber.isError,
                                isFocused = phoneNoTxtFieldState.value.phoneNumber.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(CreateContactEvent.PhoneNoFocusChange(it))
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Phone, // 👈 sets phone number keyboard
                                ),
                                onDone = true,
                                onDoneAction = {
                                    viewModel.onEvent(CreateContactEvent.SaveAndContinue)
                                }
                            )
                        }
                    }
                }
            }
            ShowUIState(
                uiState = uiState.value,
                onSuccess = {
                    println("\uD83E\uDD2D onSuccess()")
                    onSave()
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
                        delay(500)
                        viewModel.onEvent(CreateContactEvent.ClearUIState)
                    }
                }
            )
    }
}
