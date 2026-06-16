package com.example.chatter.features.contacts.presentation.create_chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatter.R
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.contacts.presentation.contacts.component.AContact
import com.example.chatter.features.contacts.presentation.create_chat.component.CreateChatTopBar
import com.example.chatter.features.contacts.presentation.model.Contact
import com.example.chatter.ui.theme.blue400
import kotlinx.coroutines.delay


@Composable
fun CreateChatScreen(
   viewModel: CreateChatViewModel,
   onSuccess: () -> Unit = {}
) {

    // Launcher to pick an image
    val contacts = viewModel.contacts
    val name = viewModel.name
    val contentResolver = LocalContext.current.contentResolver
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let{ uri ->
            contentResolver.openInputStream(uri)?.use { img ->
                img.readBytes()
            }?.let {imageByte ->
                viewModel.onEvent(CreateChatEvent.SelectImage(imageByte))
            }
        }
    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        topBar = {
            CreateChatTopBar()
        } ,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(CreateChatEvent.Done) },
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(20.dp),
                containerColor = blue400,
                contentColor = MaterialTheme.colorScheme.surface,
                content = {
                    Icon(
                        painter = painterResource(R.drawable.correct_signal_svgrepo_com) ,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                viewModel.onEvent(CreateChatEvent.Done)
                            }
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
                Column {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                                contentDescription = null,
                                tint = blue400,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable {
                                        launcher.launch("image/*")
                                    }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            TxtField(
                                modifier = Modifier,
                                hint = "Group name",
                                text = name.value.text,
                                onTextChanged = { viewModel.onEvent(CreateChatEvent.EnterName(it)) },
                                isError = name.value.isError,
                                isFocused = name.value.isFocus,
                                onFocusChanged = {
                                    viewModel.onEvent(
                                        CreateChatEvent.NameFocusChange(
                                            it
                                        )
                                    )
                                },
                                singleLine = true,
                                onDone = true,
                                onDoneAction = {
                                    viewModel.onEvent(CreateChatEvent.Done)
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Members",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = blue400,
                        )
                    }
                    LazyColumn {
                        itemsIndexed(items = contacts) { index: Int, contact: Contact ->
                            AContact(
                                contact = contact,
                                isAllowSelection = false,
                                onToggleSelection = {},
                            )
                        }
                    }
                }
                ShowUIState(
                    uiState = uiState.value,
                    onSuccess = {
                        println("\uD83E\uDD2D onSuccess()")
                        onSuccess()
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
                            delay(1000)
                            viewModel.onEvent(CreateChatEvent.ClearUIState)
                        }
                    }
                )
            }
    }
}