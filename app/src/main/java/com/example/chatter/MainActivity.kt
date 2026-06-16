package com.example.chatter

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.navigation.RootNavigationGraph
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.features.calls.data.data_source.remote.CallService
import com.example.chatter.ui.theme.ChatterTheme
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val callService: CallService by inject()
    private val userDataSource: UserDataSource by inject()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        callService.observeIncomingCall(
            scope = lifecycleScope,
            context = this
        )
        setContent {
            ChatterTheme {
                // Resolve the start destination off the main thread the first time we
                // compose. While we wait we show the loading animation so the user
                // never sees a flash of the wrong graph.
                val startDestination by produceState<Graph?>(initialValue = null) {
                    value = if (userDataSource.isTokenExpired()) {
                        Graph.Authentication
                    } else {
                        Graph.Home
                    }
                }
                val resolved = startDestination
                if (resolved == null) {
                    LoadingAnimation()
                } else {
                    val navController = rememberNavController()
                    RootNavigationGraph(
                        navController = navController,
                        startDestination = resolved
                    )
                }
            }
        }
    }
}
