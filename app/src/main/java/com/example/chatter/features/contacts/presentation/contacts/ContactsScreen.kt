package com.example.chatter.features.contacts.presentation.contacts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatter.R
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.contacts.presentation.contacts.component.AContact
import com.example.chatter.features.contacts.presentation.contacts.component.ContactsTopBar
import com.example.chatter.features.contacts.presentation.model.Contact
import com.example.chatter.ui.theme.blue400
import kotlinx.coroutines.delay

@Composable
fun ContactsScreen(
    contacts: SnapshotStateList<Contact>,
    makeChatRoom: Boolean,
    onToggleSelection: (Int) -> Unit,
    createContact: () -> Unit,
    onMakeChatRoom: (List<PhoneNumber>) -> Unit,
    makeAChat: () -> Unit,
    uiState: UIState,
    clearUIState: () -> Unit,
    onSuccess: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = { ContactsTopBar() },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            if (makeChatRoom) {
                val selected = contacts.filter { it.isSelected }
                if (selected.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            if(selected.size != 1){ onMakeChatRoom(selected.map { it.phoneNumber }) }
                            else { makeAChat()}
                        },
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = blue400,
                        contentColor = MaterialTheme.colorScheme.surface,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.correct_signal_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = createContact,
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    containerColor = blue400,
                    contentColor = MaterialTheme.colorScheme.surface,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn {
                itemsIndexed(items = contacts) { index, contact ->
                    AContact(
                        contact = contact,
                        isAllowSelection = makeChatRoom,
                        onToggleSelection = {
                            if (makeChatRoom) {
                                onToggleSelection(index)
                            }
                        },
                    )
                }
            }
            ShowUIState(
                uiState = uiState,
                onSuccess = { onSuccess() },
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
                        delay(1000)
                        clearUIState()
                    }
                }
            )
        }
    }
}
