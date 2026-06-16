package com.example.chatter.features.auth.presentation.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.core.domain.model.User
import com.example.chatter.core.presentation.model.FocusedErrorTxtFieldState
import com.example.chatter.core.presentation.model.PhoneNumTxtFieldState
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.auth.domain.use_case.AuthUseCases
import com.example.chatter.features.auth.domain.use_case.GetAllUsers
import com.example.chatter.features.auth.domain.use_case.IsValidEmailAddress
import com.example.chatter.features.auth.domain.use_case.SendCodeForSignUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authUseCases: AuthUseCases
): ViewModel() {

    private var _dialogState = mutableStateOf(false)
    val dialogState : State<Boolean> = _dialogState

    private var _username = mutableStateOf(FocusedErrorTxtFieldState())
    val username : State<FocusedErrorTxtFieldState> = _username

    private var _password = mutableStateOf(FocusedErrorTxtFieldState())
    val password : State<FocusedErrorTxtFieldState> = _password

    private var _phoneNumber = mutableStateOf(PhoneNumTxtFieldState())
    val phoneNumber : State<PhoneNumTxtFieldState> = _phoneNumber

    private var _email = mutableStateOf(FocusedErrorTxtFieldState())
    val email : State<FocusedErrorTxtFieldState> = _email

    private var _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState : StateFlow<UIState> = _uiState

    val isSignUp = derivedStateOf {
        username.value.text.isNotEmpty() &&
                password.value.text.isNotEmpty() &&
                phoneNumber.value.phoneNumber.text.isNotEmpty() &&
                email.value.text.isNotEmpty() &&
                !email.value.isError &&
                password.value.text.length >=6
    }

    private var users = listOf<User>()
    init {
        viewModelScope.launch {
            users = authUseCases.getAllUsers()
        }
    }

    fun onEvent(event : SignupEvents){
        when(event){
            is SignupEvents.Signup -> {
                if(isSignUp.value){
                    viewModelScope.launch {
                        _uiState.value = UIState.Loading
                        authUseCases.sendCodeForSignUp(
                            username = _username.value.text,
                            password = _password.value.text,
                            countryCode = _phoneNumber.value.country.dialCode.toString(),
                            phoneNumber = _phoneNumber.value.phoneNumber.text,
                            email = _email.value.text
                        ).fold(
                            onSuccess = {_uiState.value = UIState.Success },
                            onFailure = {_uiState.value = UIState.Error(it.message.toString())}
                        )
                    }
                }else{
                    _uiState.value = UIState.Error("Please enter all fields and password should be greater than 6 character length")
                }
            }
            is SignupEvents.DialogStateChanged -> {
                _dialogState.value = event.state
            }
            is SignupEvents.EnterEmail -> {
                _email.value = _email.value.copy(text = event.text , isError = authUseCases.isValidEmailAddress(event.text) )
            }
            is SignupEvents.EnterPassword -> {
                _password.value = _password.value.copy(text = event.text , isError = event.text.isEmpty())
            }
            is SignupEvents.EnterPhoneNumber -> {
                val isUserExist = users.find { it ->
                        it.phoneNumber.phoneNumber == event.text
                        && it.phoneNumber.countryCode == _phoneNumber.value.country.dialCode.toString()
                }
                _phoneNumber.value = _phoneNumber.value.copy(phoneNumber = _phoneNumber.value.phoneNumber.copy(text = event.text , isError = isUserExist!= null)  )
                if(isUserExist!= null){
                    _uiState.value = UIState.Error("User already exist")
                }
            }
            is SignupEvents.EnterUsername -> {
                val isUserExist = users.find { it -> it.username == event.text }
                _username.value = _username.value.copy(text = event.text , isError = event.text.isEmpty() || isUserExist!= null)
                if(isUserExist!= null){
                    _uiState.value = UIState.Error("User already exist")
                }
            }
            is SignupEvents.EmailFocusChanged -> {
                _email.value = _email.value.copy(isFocus = event.focusState.isFocused)
            }
            is SignupEvents.PasswordFocusChanged -> {
                _password.value = _password.value.copy(isFocus = event.focusState.isFocused)
            }
            is SignupEvents.PhoneNumberFocusChanged -> {
                _phoneNumber.value = _phoneNumber.value.copy(phoneNumber = _phoneNumber.value.phoneNumber.copy(isFocus = event.focusState.isFocused))
            }
            is SignupEvents.UsernameFocusChanged -> {
                _username.value = _username.value.copy(isFocus = event.focusState.isFocused)
            }
            is SignupEvents.ClearUIState -> {
                _uiState.value = UIState.Idle
            }
            is SignupEvents.CountryChanged -> {
                _phoneNumber.value = _phoneNumber.value.copy(country = event.country)
            }
        }
    }
}