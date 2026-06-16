package com.example.chatter

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.core.di.coreModule
import com.example.chatter.features.auth.di.authModule
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import com.example.chatter.features.calls.di.callModule
import com.example.chatter.features.chats.data.data_source.local.ImageStorageWorker
import com.example.chatter.features.chats.data.data_source.local.UpdateUnreadMessagesWorker
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.chats.di.chatsModule
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import com.example.chatter.features.contacts.di.contactsModule
import com.example.chatter.features.notifications.data.data_source.remote.CustomNotificationHandler
import com.example.chatter.features.notifications.di.notificationAppModule
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.video.android.core.notifications.NotificationConfig
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.option.viewModelScopeFactory
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration


/**
 * Our stream video client need our username to initialize
 * so we can not just create at once when the application is launched
 * because we only knows the user name during run time .
 *
 * Alright bro 😎 let’s break this all down **super simply and clearly**, so you understand not just *what* these classes are — but also *why* they exist and *how* they connect together.
 *
 * ---
 *
 * ### 💬 The Big Picture
 *
 * These classes are **data models** used in a **video or voice call system** (like Zoom, Meet, or Stream Video SDK).
 * Each one represents a **specific piece of call-related data** — such as:
 *
 * * who the users are,
 * * what their status is,
 * * what call they belong to,
 * * and some details about the call itself.
 *
 * They’re marked `@Stable` to tell **Jetpack Compose** that these objects are *immutable and predictable*, so the UI doesn’t recompose unnecessarily when using them.
 *
 * ---
 *
 * ### 🧑‍🤝‍🧑 1. `CallUser`
 *
 * Represents **a person (user)** who can participate in a call.
 *
 * ```kotlin
 * @Stable
 * data class CallUser(
 *     val id: String,           // Unique ID of the user (like a user ID)
 *     val name: String,         // User's name
 *     val role: String,         // Role in the call (e.g., "host", "guest", "viewer")
 *     val imageUrl: String,     // Profile picture URL
 *     val teams: List<String>,  // List of teams or groups this user belongs to
 *     val state: CallUserState?,// Their current status in the call (muted, video off, etc.)
 *     val createdAt: Date?,     // When the user was created in the system
 *     val updatedAt: Date?,     // When user info was last updated
 * )
 * ```
 *
 * 🧠 Think of this like a **user profile** that includes real-time state info.
 *
 * ---
 *
 * ### ⚙️ 2. `CallUserState`
 *
 * Represents **what the user is currently doing in the call**.
 *
 * ```kotlin
 * @Stable
 * data class CallUserState(
 *     val trackIdPrefix: String, // Internal ID used for media tracks (audio/video)
 *     val online: Boolean,       // Is the user connected right now?
 *     val audio: Boolean,        // Is their microphone ON?
 *     val video: Boolean,        // Is their camera ON?
 * )
 * ```
 *
 * 🧠 This helps the app know:
 *
 * * who is muted 🎤
 * * who has camera off 📷
 * * who is online/offline 🟢⚫
 *
 * ---
 *
 * ### 📞 3. `CallMember`
 *
 * Represents **a user’s membership in a specific call**.
 *
 * ```kotlin
 * @Stable
 * data class CallMember(
 *     val callCid: String,     // Unique ID of the call (like "call:123")
 *     val role: String,        // Role in this particular call (could differ from global role)
 *     val userId: String,      // The user's ID
 *     val createdAt: Date?,    // When they joined
 *     val updatedAt: Date?,    // When their membership info last changed
 * )
 * ```
 *
 * 🧠 This basically connects a **user to a specific call** — like saying:
 * “User 123 is a moderator in Call 789.”
 *
 * ---
 *
 * ### 📲 4. `CallInfo`
 *
 * Represents **general information about the call itself**.
 *
 * ```kotlin
 * @Stable
 * data class CallInfo(
 *     val cid: String,                 // Unique call ID (e.g., "default:call123")
 *     val type: String,                // Type of call ("audio", "video", "broadcast")
 *     val id: String,                  // Internal ID of the call
 *     val createdByUserId: String,     // Who started the call
 *     val broadcastingEnabled: Boolean,// Whether broadcasting is on (live to others)
 *     val recordingEnabled: Boolean,   // Whether the call is being recorded
 *     val createdAt: Date?,            // When the call started
 *     val updatedAt: Date?,            // When info was last updated
 *     val custom: Map<String, Any?>,   // Any extra custom data (like topic, duration, etc.)
 * )
 * ```
 *
 * 🧠 Think of this as the **metadata of the call** —
 * like a card describing what kind of call it is, who made it, and if it’s being recorded.
 *
 * ---
 *
 * ### 📋 5. `CallDetails`
 *
 * Represents **extra info about participants and permissions**.
 *
 * ```kotlin
 * @Stable
 * data class CallDetails(
 *     val memberUserIds: List<String>, // IDs of all users in the call
 *     val members: Map<String, CallUser>, // Detailed user info mapped by ID
 *     val ownCapabilities: List<OwnCapability>, // Permissions for the current user
 * )
 * ```
 *
 * 🧠 So, if you’re in a call:
 *
 * * `memberUserIds` → ["u1", "u2", "u3"]
 * * `members` → map of user details (profiles + states)
 * * `ownCapabilities` → what **you** are allowed to do (e.g., mute others, share screen, etc.)
 *
 * ---
 *
 * ### 🎥 6. `CallEgress`
 *
 * Represents **where the call output is going** — like broadcast or recording destinations.
 *
 * ```kotlin
 * @Stable
 * data class CallEgress(
 *     val broadcastEgress: String, // Where the live broadcast is sent (e.g., YouTube)
 *     val recordEgress: String,    // Where recordings are stored (e.g., S3 bucket)
 * )
 * ```
 *
 * 🧠 “Egress” = “output direction.”
 * So this tells the app: where is the call’s audio/video going out to?
 *
 * ---
 *
 * ### 🧩 Summary Table
 *
 * | Class           | Represents                 | Key Idea                               |
 * | --------------- | -------------------------- | -------------------------------------- |
 * | `CallUser`      | A person in the system     | User profile                           |
 * | `CallUserState` | User’s live status         | Online/audio/video info                |
 * | `CallMember`    | Link between user & call   | Defines membership and role            |
 * | `CallInfo`      | Metadata about the call    | Type, creator, recording, etc.         |
 * | `CallDetails`   | Participants & permissions | Who’s in the call and what they can do |
 * | `CallEgress`    | Output of the call         | Broadcast or recording destination     |
 *
 * ---
 *
 * Would you like me to also show how these classes might be **used together in a real call example** (like showing how `CallInfo`, `CallUser`, and `CallDetails` link in code)?
 * That’ll help everything “click” together visually.
 *
 *
 * Alright bro 😎 — let’s make this **super easy** to understand.
 * The `StreamVideoBuilder` is basically the **“starter kit”** for creating your **video call client** in the Stream SDK — it sets up *everything* your app needs to start connecting, joining, and managing calls.
 *
 * ---
 *
 * ## 🧱 What Is It?
 *
 * `StreamVideoBuilder` → A **builder class** used to create and configure a `StreamVideo` client.
 * This client is like the **main brain** 🧠 of your calling system — it handles:
 *
 * * Connecting to the Stream server 🌐
 * * Authenticating the user 🔐
 * * Managing calls (start, join, leave) 📞
 * * Handling notifications 🔔
 * * Running foreground services ⚙️
 *
 * So when you write:
 *
 * ```kotlin
 * val client = StreamVideoBuilder(
 *     context = context,
 *     apiKey = apiKey,
 *     geo = GEO.GlobalEdgeNetwork,
 *     user = user,
 *     token = token,
 *     loggingLevel = LoggingLevel.BODY
 * )
 * ```
 *
 * You’re saying:
 *
 * > “Hey SDK, set up everything for me — here’s my app info, my user, and how I want you to behave.”
 *
 * ---
 *
 * ## ⚙️ Each Property Explained Clearly
 *
 * Let’s go one by one 👇
 *
 * | Property                         | Type                                | Simple Explanation                                                                                                                    |
 * | -------------------------------- | ----------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
 * | **context**                      | `Context`                           | Android app context. Required so SDK can access system features (e.g., permissions, notifications).                                   |
 * | **apiKey**                       | `ApiKey`                            | Unique key that identifies *your app* on Stream servers (you get it from the Stream dashboard).                                       |
 * | **geo**                          | `GEO`                               | Region setting — tells Stream which server cluster to connect to (e.g., Global, EU, US). Used for performance and privacy/geofencing. |
 * | **user**                         | `User`                              | The currently logged-in user — can be a **normal**, **guest**, or **anonymous** user. Contains their ID, name, etc.                   |
 * | **token**                        | `UserToken`                         | The secure token generated by your backend to authenticate this user.                                                                 |
 * | **tokenProvider**                | `(suspend (Throwable?) -> String)?` | Optional. Used if your token **expires** — SDK will call this function to ask your server for a **fresh token** without logging out.  |
 * | **loggingLevel**                 | `LoggingLevel`                      | Controls how much SDK logs in the console (e.g., `BODY` shows detailed network requests). Useful for debugging.                       |
 * | **notificationConfig**           | `NotificationConfig`                | Configuration for **push notifications** (sound, vibration, channel name, etc.).                                                      |
 * | **ringNotification**             | `((call: Call) -> Notification?)?`  | Lets you **customize incoming call notifications** (for example, use your own ringtone or notification layout).                       |
 * | **connectionTimeoutInMs**        | `Long`                              | How long (in milliseconds) to wait before giving up on connecting to Stream’s server (default 10 seconds).                            |
 * | **ensureSingleInstance**         | `Boolean`                           | Makes sure **only one client instance** exists. Prevents bugs from multiple connections at once.                                      |
 * | **videoDomain**                  | `String`                            | Base URL for Stream’s video service (you can change it for testing with a local server). Normally you leave this alone.               |
 * | **runForegroundServiceForCalls** | `Boolean`                           | When true → SDK runs a **foreground service** while you’re in a call so Android doesn’t kill the process (important for long calls).  |
 * | **localSfuAddress**              | `String?`                           | Used for **testing only**. Lets you connect to a local media server (SFU = Selective Forwarding Unit). Usually null.                  |
 * | **sounds**                       | `Sounds`                            | Allows you to **replace default SDK sounds** (ringtone, hangup sound, etc.).                                                          |
 * | **crashOnMissingPermission**     | `Boolean`                           | If true → SDK throws an error if camera/mic permissions are missing (helps catch setup mistakes early).                               |
 * | **permissionCheck**              | `StreamPermissionCheck`             | Logic to check if required permissions (camera, mic) are granted before joining a call. You can override with your own logic.         |
 *
 * ---
 *
 * ## 🧩 Why Use a *Builder*?
 *
 * Instead of passing 15 parameters to a constructor (which is messy), the **builder pattern** lets you:
 *
 * * Clearly define what each property does
 * * Provide **default values** for optional parameters
 * * Keep your setup **readable and flexible**
 *
 * So this:
 *
 * ```kotlin
 * val client = StreamVideoBuilder(context, apiKey, user, token)
 * ```
 *
 * is much cleaner than:
 *
 * ```kotlin
 * val client = StreamVideo(context, apiKey, GEO.GlobalEdgeNetwork, user, token, null, LoggingLevel(), ...)
 * ```
 *
 * ---
 *
 * ## 🧠 Conceptually
 *
 * When you call `StreamVideoBuilder(...)`, you’re saying:
 *
 * > “Set up my app to connect to Stream’s video service using this user, in this region, with these permissions, logs, and behaviors.”
 *
 * Then you usually finalize it with:
 *
 * ```kotlin
 * val streamVideo = client.build()
 * ```
 *
 * Now `streamVideo` is your **main entry point** for everything:
 *
 * * Making or joining calls
 * * Listening to call state updates
 * * Managing participants
 * * Handling incoming notifications
 *
 * ---
 *
 * ## 🪄 In Simple Words
 *
 * Think of `StreamVideoBuilder` as your **remote setup wizard**:
 *
 * * You give it your **credentials** (`apiKey`, `token`, `user`),
 * * Tell it **how you want things to behave** (logging, sounds, notifications),
 * * And it returns a **ready-to-use video client** that handles everything behind the scenes.
 *
 * ---
 *
 * Would you like me to show you a **real-world example** of how to use the `StreamVideoBuilder` to initialize the client and join a call step-by-step (with code + flow explanation)? That’ll make everything connect perfectly.
 *
 */


@OptIn(KoinExperimentalAPI::class)
class ChatterApp : Application() , Configuration.Provider , KoinStartup{

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun onKoinStartup(): KoinConfiguration  = koinConfiguration{
        androidContext(this@ChatterApp)
        androidLogger()
        modules(coreModule, contactsModule , authModule , callModule, chatsModule ,
            notificationAppModule
        )
        options(viewModelScopeFactory())
        workManagerFactory()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        println("🚀 ChatterApp: onCreate started")

        // 1️⃣ Install local data stores
        println("🗄️ Installing UserDataSource and StreamUserDataStore")
        UserDataSource.install(this , isEncrypted = true)
        StreamUserDataStore.install(this , isEncrypted = true)

        val customNotificationHandler : CustomNotificationHandler by inject()
        println("🔑 Koin: UserDataSource injected successfully")
        val chatterVideoInitHelper : ChatterVideoInitHelper by inject ()
        val userDataSource : UserDataSource by inject()
        val userSocketService : PresenceSocketService by inject()
        val lifecycleObserver : AppLifecycleObserver  = AppLifecycleObserver(
            userSocketService = userSocketService,
            userDataSource = userDataSource,
            scope = scope
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            lifecycleObserver
        )


        val notificationConfig = NotificationConfig(
            // Make the notification low priority if the app is in foreground, so its not visible as a popup, since we want to handle
            // the incoming call in full screen when app is running.
            hideRingingNotificationInForeground = true,
            pushDeviceGenerators = listOf( FirebasePushDeviceGenerator(
                providerName =  "chatter_firebase", context = this),
            ) ,
            notificationHandler = customNotificationHandler,
        )

        chatterVideoInitHelper.init(this@ChatterApp,
            notificationConfig,)

        val streamUserDataStore = StreamUserDataStore.instance()
        scope.launch{
            if(StreamUserDataStore.isInstalled){
                println("🔑 ChatterApp StreamUserDataStore has bee installed")
                val userPreference = streamUserDataStore.data.firstOrNull()
                if(userPreference != null){
                    println("🔑 ChatterApp  userPreferences : ${userPreference.toString()}")
                    chatterVideoInitHelper.loadSdk(
                        data = userPreference,
                    )
                }else{
                    println("🔑 ChatterApp  userPreferences is null")
                    val user = userDataSource.user.firstOrNull() ?: return@launch
                    Firebase.messaging.token
                        .addOnSuccessListener { token ->
                            launch {
                                chatterVideoInitHelper.initChatterVideo(
                                    phoneNumber = user.phoneNumber,
                                    localToken = token
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            println("🔑 ChatterApp FCM Token failed: ${e.message}")
                        }
                }
            }
        }
    }
}



