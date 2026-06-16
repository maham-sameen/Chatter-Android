package com.example.chatter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppLifecycleObserver(
    private val userSocketService: PresenceSocketService,
    private val userDataSource: UserDataSource ,
    private val scope : CoroutineScope
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        println("[AppLifecycleObserver] , onStart()")
        // App comes to foreground ✅
        owner.lifecycleScope.launch {
             println("🟢[AppLifecycleObserver]  App Foreground → Init Socket")
             val user = userDataSource.user.firstOrNull()
            println("🟢[AppLifecycleObserver]  App Foreground → user  ${user.toString()}")
             if(user != null){
                 userSocketService.initSession(
                     UserSession(
                         userPhone = user.phoneNumber.toString(),
                         contactPhone = emptyList()
                     ),
                     scope = scope
                 )
             }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        println("[AppLifecycleObserver] , onStop()")
        // App goes to background ✅
        owner.lifecycleScope.launch {
            println("🔴 App Background → Close Socket")
            userSocketService.closeSession()
        }
    }
}
