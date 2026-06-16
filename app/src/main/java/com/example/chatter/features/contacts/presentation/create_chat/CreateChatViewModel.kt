package com.example.chatter.features.contacts.presentation.create_chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.chatter.core.presentation.model.FocusedErrorTxtFieldState
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.contacts.domain.use_case.create_chat.CreateChatUseCase
import com.example.chatter.features.contacts.presentation.model.Contact
import com.example.chatter.features.contacts.presentation.util.CreateChat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class CreateChatViewModel(
    private val createChatUseCase: CreateChatUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val contacts: SnapshotStateList<Contact> = mutableStateListOf()

    private val _name = mutableStateOf(FocusedErrorTxtFieldState())
    val name: State<FocusedErrorTxtFieldState> = _name

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState

    private var pickedImage: ByteArray? = null

    init {
        val route = savedStateHandle.toRoute<CreateChat>()
        val phoneNumbers = Json.decodeFromString<List<PhoneNumber>>(route.contactsPhoneNumbers)
        viewModelScope.launch {
            val loaded = createChatUseCase.getAllContactsViaPhoneNo(phoneNumbers)
            contacts.addAll(loaded)
            if (loaded.size == 1) {
                handleDone()
            }
        }
    }

    fun onEvent(event: CreateChatEvent) {
        when (event) {
            is CreateChatEvent.EnterName -> {
                _name.value = _name.value.copy(text = event.text, isError = false)
            }
            is CreateChatEvent.NameFocusChange -> {
                _name.value = _name.value.copy(isFocus = event.focusState.isFocused)
            }
            is CreateChatEvent.SelectImage -> {
                pickedImage = event.image
            }
            CreateChatEvent.Done -> handleDone()
            CreateChatEvent.ClearUIState -> _uiState.value = UIState.Idle
        }
    }

    private fun handleDone() {
        val isGroup = contacts.size > 1
        if (isGroup && _name.value.text.isBlank()) {
            _name.value = _name.value.copy(isError = true)
            _uiState.value = UIState.Error("Group name required")
            return
        }
        _uiState.value = UIState.Loading
        viewModelScope.launch {
            createChatUseCase.insertChatRoom(
                name = _name.value.text,
                image = pickedImage,
                contacts = contacts
            ).fold(
                onSuccess = { _uiState.value = UIState.Success },
                onFailure = { _uiState.value = UIState.Error(it.message ?: "Failed to create chat") }
            )
        }
    }
}
