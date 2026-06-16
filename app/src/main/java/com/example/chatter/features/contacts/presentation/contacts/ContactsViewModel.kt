package com.example.chatter.features.contacts.presentation.contacts

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.contacts.domain.use_case.GetAllContacts
import com.example.chatter.features.contacts.domain.use_case.create_chat.CreateChatUseCase
import com.example.chatter.features.contacts.presentation.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val getAllContacts: GetAllContacts ,
    private val createChatUseCase: CreateChatUseCase
) : ViewModel() {

    val editContacts: StateFlow<SnapshotStateList<com.example.chatter.features.contacts.presentation.model.Contact>> =
        getAllContacts()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                mutableStateListOf()
            )

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.OnToggleSelection -> {
                val list = editContacts.value
                if (event.index in list.indices) {
                    list[event.index] = list[event.index].copy(
                        isSelected = !list[event.index].isSelected
                    )
                }
            }
            ContactsEvent.CreateAChat -> {
                _uiState.value = UIState.Idle
                viewModelScope.launch {
                    createChatUseCase.insertChatRoom(
                        name = "",
                        image = null,
                        contacts = mutableStateListOf<Contact>().apply {
                            addAll(editContacts.value.filter { it.isSelected })
                        }
                    ).fold(
                        onSuccess = { _uiState.value = UIState.Success },
                        onFailure = { _uiState.value = UIState.Error(it.message ?: "Failed to create chat") }
                    )
                }
            }
            ContactsEvent.Action -> Unit
            ContactsEvent.ClearUIState -> _uiState.value = UIState.Idle
        }
    }
}
