package com.example.chatter.core.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.features.HomeScreen
import com.example.chatter.features.auth.authNavigation


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun RootNavigationGraph(
    navController : NavHostController ,
    startDestination : Any
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavigation(
            navController = navController,
            goHome = {
                navController.navigate(Graph.Home){
                    popUpTo(Graph.Home){
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )

        composable<Graph.Home>{
            HomeScreen(
                logOut = {
                    navController.navigate(Graph.Authentication){
                        popUpTo(Graph.Authentication){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}