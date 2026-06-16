package com.example.chatter.features

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chatter.Constants
import com.example.chatter.core.presentation.ChatterEvent
import com.example.chatter.core.presentation.SessionExpiredDialog
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.core.presentation.utils.navigate
import com.example.chatter.features.utils.BottomBarScreen
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun HomeScreen(logOut : () -> Unit) {
    val navController = rememberNavController()
    val screens = listOf(
        BottomBarScreen.Chats,
        BottomBarScreen.Status,
        BottomBarScreen.Contacts,
        BottomBarScreen.Calls
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = Constants.screenRoutes.any { it == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if(bottomBarDestination) {
                BottomBar(
                    screens = screens,
                    selected = { screen ->
                        currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true
                    },
                    onClick = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.surface
        ) {
            HomeNavGraph(
                navController = navController,
                logOut = logOut
            )
        }
    }
}