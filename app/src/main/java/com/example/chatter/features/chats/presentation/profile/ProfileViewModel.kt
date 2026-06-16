package com.example.chatter.features.chats.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.chats.domain.use_case.profile.ProfileUseCases
import com.example.chatter.features.chats.presentation.profile.model.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileUseCase: ProfileUseCases ,
    private val workManager: WorkManager
) : ViewModel() {

    val profile : StateFlow<ProfileState> = profileUseCase
        .getUser()
        .onStart {
            _uiState.value = UIState.Loading
        }
        .onEach { _uiState.value = UIState.Idle }
        .catch { println(it.message.toString()) }
        .stateIn(viewModelScope , SharingStarted.WhileSubscribed(500) , profileUseCase.getDefaultUser())


    private var _uiState : MutableStateFlow<UIState>  = MutableStateFlow(UIState.Idle)
    val uiState : StateFlow<UIState> = _uiState


    fun onEvent(event : ProfileEvents){
        when(event){
            is ProfileEvents.SelectImage -> {
                viewModelScope.launch{
                    profileUseCase.updateUserProfile(
                        event.image,
                        workManager = workManager
                    )
                }
            }
            ProfileEvents.ClearUIState -> {
                _uiState.value = UIState.Idle
            }
            is ProfileEvents.Logout -> {

            }
        }
    }

}