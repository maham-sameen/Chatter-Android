package com.example.chatter.features.chats.presentation.profile

import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.Profile
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.chats.presentation.profile.component.ProfileAppBar
import com.example.chatter.features.chats.presentation.profile.component.UserInfos
import com.example.chatter.ui.theme.blue400
import kotlinx.coroutines.delay
import com.example.chatter.R


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    logOut : () -> Unit ,
    onBackPressed : () -> Unit
) {
    val contentResolver = LocalContext.current.contentResolver
    val profile =  viewModel.profile.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { it ->
        it?.let{ uri ->
            contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            }?.let {imageByte ->
                viewModel.onEvent(ProfileEvents.SelectImage(imageByte))
            }
        }
    }
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        topBar = {
            ProfileAppBar(
                onBackPressed = onBackPressed
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)}
    ) { innerPadding ->
        Surface (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) , 
        ){
            Column(
                modifier = Modifier.fillMaxWidth() ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically ,
                    horizontalArrangement = Arrangement.Center
                ){
                    Profile(
                        modifier = Modifier.size(130.dp),
                        profile = profile.value.image?.let {
                            {
                                println("image $it")
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null ,
                                    contentScale = ContentScale.Crop ,
                                    modifier = Modifier.fillMaxSize(), // ✅ this makes it fill the circle
                                )
                            }
                        },
                        color = profile.value.colorScheme.background,
                        firstCapitalLetterTxt = {
                            Text(
                                text = profile.value.userDetails[0].value[0].uppercase(),
                                color = profile.value.colorScheme.icon,
                                fontSize = 30.sp,
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(R.drawable.outline_add_photo_alternate_24) ,
                        contentDescription = null ,
                        tint = blue400 ,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                UserInfos(profile.value.userDetails)
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = logOut ,
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            containerColor = blue400
                        )
                ) {
                    Text(
                        text = "Logout" ,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
        ShowUIState(
            uiState = uiState.value,
            onSuccess = {},
            showLoading = { LoadingAnimation() },
            showMessage = {
                    message ->
                LaunchedEffect(message) {
                    showSnackBarUI(
                        snackBarHostState = snackBarHostState,
                        message = message,
                        actionLabel = "",
                        onDismiss = {},
                        onActionPerformed = {}
                    )
                    delay(1000)
                    viewModel.onEvent(ProfileEvents.ClearUIState)
                }
            }
        )
    }
}


