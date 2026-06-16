package com.example.chatter.features.contacts.presentation.create_contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.core.presentation.model.FocusedErrorTxtFieldState
import com.example.chatter.core.presentation.model.PhoneNumTxtFieldState
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.contacts.domain.use_case.create_contact.CreateContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateContactViewModel(
    private val createContactUseCase: CreateContactUseCase
) : ViewModel() {

    private val _name = mutableStateOf(FocusedErrorTxtFieldState())
    val name: State<FocusedErrorTxtFieldState> = _name

    private val _phoneNumber = mutableStateOf(PhoneNumTxtFieldState())
    val phoneNumber: State<PhoneNumTxtFieldState> = _phoneNumber

    private val _isSaveAndContinue = mutableStateOf(false)
    val isSaveAndContinue: State<Boolean> = _isSaveAndContinue

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState

    fun onEvent(event: CreateContactEvent) {
        when (event) {
            is CreateContactEvent.EnterName -> {
                _name.value = _name.value.copy(text = event.text, isError = event.text.isEmpty())
                refreshSaveEnabled()
            }
            is CreateContactEvent.EnterPhoneNo -> {
                val current = _phoneNumber.value.phoneNumber.text
                val isValid = current.isEmpty() || createContactUseCase.isValidPhoneNo(event.text , regionCode = _phoneNumber.value.country.isoCode)
                _phoneNumber.value = _phoneNumber.value.copy(
                    phoneNumber = _phoneNumber.value.phoneNumber.copy(
                        text = event.text,
                        isError = event.text.isNotEmpty() && !isValid
                    ),
                )
                refreshSaveEnabled()
            }
            is CreateContactEvent.ChangeCountry -> {
                _phoneNumber.value = _phoneNumber.value.copy(country = event.country)
                refreshSaveEnabled()
            }
            is CreateContactEvent.NameFocusChange -> {
                _name.value = _name.value.copy(isFocus = event.focusState.isFocused)
            }
            is CreateContactEvent.PhoneNoFocusChange -> {
                _phoneNumber.value = _phoneNumber.value.copy(
                    phoneNumber = _phoneNumber.value.phoneNumber.copy(
                        isFocus = event.focusState.isFocused
                    )
                )
            }
            CreateContactEvent.SaveAndContinue -> handleSave()
            CreateContactEvent.ClearUIState -> _uiState.value = UIState.Idle
        }
    }

    private fun refreshSaveEnabled() {
        _isSaveAndContinue.value = _name.value.text.isNotBlank() &&
                _phoneNumber.value.phoneNumber.text.isNotBlank() &&
                !_phoneNumber.value.phoneNumber.isError
    }

    private fun handleSave() {
        if (!_isSaveAndContinue.value) {
            _uiState.value = UIState.Error("Enter a valid name and phone number")
            return
        }
        _uiState.value = UIState.Loading
        viewModelScope.launch {
            createContactUseCase.insertContact(
                name = _name.value.text,
                phoneNumber = PhoneNumber(
                    countryCode = "+${_phoneNumber.value.country.dialCode}",
                    phoneNumber = _phoneNumber.value.phoneNumber.text
                )
            ).fold(
                onSuccess = { _uiState.value = UIState.Success },
                onFailure = {
                    _uiState.value = UIState.Error(it.message ?: "Failed to save contact")
                }
            )
        }
    }
}
