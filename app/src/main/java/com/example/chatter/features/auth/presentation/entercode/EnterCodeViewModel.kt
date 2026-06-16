package com.example.chatter.features.auth.presentation.entercode

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.core.presentation.model.FocusedErrorTxtFieldState
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.auth.domain.use_case.AuthUseCases
import com.example.chatter.features.auth.domain.use_case.CheckCodeForSignIn
import com.example.chatter.features.auth.domain.use_case.CheckCodeForSignUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnterCodeViewModel(
  private val authUseCases: AuthUseCases
) : ViewModel() {

    private var _code = mutableStateOf(FocusedErrorTxtFieldState())
    val code : State<FocusedErrorTxtFieldState> = _code

    private var _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState : StateFlow<UIState> = _uiState

    fun onEvent(event : EnterCodeEvents){
        when(event){
            is EnterCodeEvents.EnterCode -> {
                _code.value = _code.value.copy(text = event.text , isError = event.text.isEmpty())
            }
            is EnterCodeEvents.CodeFocusChanged -> {
                _code.value = _code.value.copy(isFocus = event.focusState.isFocused)
            }
            is EnterCodeEvents.Verify -> {
                if(_code.value.text.isEmpty()){
                    _uiState.value = UIState.Error("Please enter code")
                    return
                }
                viewModelScope.launch{
                    _uiState.value = UIState.Loading
                    if (event.username != null) {
                        authUseCases.checkCodeForSignIn(event.username, _code.value.text)
                            .fold(
                                onSuccess = {
                                    _uiState.value = UIState.Success
                                },
                                onFailure = {
                                    _uiState.value = UIState.Error(it.message ?: "Unknown Error")
                                }
                            )
                    }else if(event.phoneNumber != null){
                        authUseCases.checkCodeForSignUp(event.phoneNumber.countryCode , event.phoneNumber.phoneNumber , _code.value.text)
                            .fold(
                                onSuccess = { _uiState.value = UIState.Success },
                                onFailure = {
                                    _uiState.value = UIState.Error(it.message ?: "Unknown Error")
                                }
                            )
                    }
                }
            }
            is EnterCodeEvents.ClearUIState -> {
                _code.value = _code.value.copy(isError = false)
            }
            is EnterCodeEvents.Resend -> {}
        }
    }
}