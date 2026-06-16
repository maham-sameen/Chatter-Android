package com.example.chatter.features.calls.presentation.call_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.calls.domain.use_case.CallHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CallHistoryViewModel(
    private val callHistoryUseCase: CallHistoryUseCase
): ViewModel() {

    private var _uiState : MutableStateFlow<UIState> = MutableStateFlow(UIState.Idle)
    val uiState : StateFlow<UIState> = _uiState


    @OptIn(ExperimentalCoroutinesApi::class)
    val calls = callHistoryUseCase.getCurrentUserId()
        .flatMapLatest {
            callHistoryUseCase.getCallHistory(it)
        }.cachedIn(viewModelScope)

    fun clearUIState(){
        viewModelScope.launch {
            _uiState.value = UIState.Idle
        }
    }
}