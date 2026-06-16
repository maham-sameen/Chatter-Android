package com.example.chatter.features.auth.presentation.signin

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.core.presentation.model.FocusedErrorTxtFieldState
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.auth.domain.use_case.AuthUseCases
import com.example.chatter.features.auth.domain.use_case.SendCodeForSignIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SigninViewModel(
    private val authUseCases: AuthUseCases
)  : ViewModel() {

    private var _username = mutableStateOf(FocusedErrorTxtFieldState())
    val username : State<FocusedErrorTxtFieldState> = _username

    private var _password = mutableStateOf(FocusedErrorTxtFieldState())
    val password : State<FocusedErrorTxtFieldState> = _password

    private var _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState : StateFlow<UIState> = _uiState

    val isSignIn = derivedStateOf {
        username.value.text.isNotEmpty() &&
                password.value.text.isNotEmpty()
    }

    fun onEvent(event : SigninEvents){
        when(event){
          is SigninEvents.Signin -> {
              if(isSignIn.value){
                  viewModelScope.launch{
                      _uiState.value = UIState.Loading
                      authUseCases.sendCodeForSignIn(
                          username = _username.value.text,
                          password = _password.value.text
                      ).fold(
                          onSuccess = {_uiState.value = UIState.Success },
                          onFailure = {_uiState.value = UIState.Error(it.message.toString())}
                      )
                  }
              }else{
                  _uiState.value = UIState.Error("Please enter all fields")
              }
          }
          is SigninEvents.EnterPassword -> {
              _password.value = _password.value.copy(text = event.text , isError = event.text.isEmpty())
          }
          is SigninEvents.EnterUsername -> {
              _username.value = _username.value.copy(text = event.text , isError = event.text.isEmpty())
          }
          is SigninEvents.PasswordFocusChanged -> {
              _password.value = _password.value.copy(isFocus = event.focusState.isFocused)
          }
          is SigninEvents.UsernameFocusChanged -> {
              _username.value = _username.value.copy(isFocus = event.focusState.isFocused)
          }
          is SigninEvents.ClearUIState -> {
              _uiState.value = UIState.Idle
          }
        }
    }
}