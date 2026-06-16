package com.example.chatter.features.chats.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.features.chats.presentation.profile.ProfileScreen
import com.example.chatter.features.chats.presentation.profile.ProfileViewModel
import com.example.chatter.features.chats.presentation.utils.Settings
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.settingNavGraph(
    navController : NavHostController ,
    logOut : () -> Unit ,
    onBackPressed : () -> Unit
) {
    navigation<Graph.Settings>(startDestination = Settings.Profile){
        composable<Settings.Profile> {
            ProfileScreen(
                logOut = logOut,
                viewModel = koinViewModel<ProfileViewModel>(),
                onBackPressed = onBackPressed
            )
        }
    }
}