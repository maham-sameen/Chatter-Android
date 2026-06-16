package com.example.chatter.features.calls.data.utils

import android.content.Context
import com.example.chatter.Constants
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.calls.data.data_source.model.remote.CreateUserRequest
import com.example.chatter.features.calls.data.data_source.remote.CallDevice
import com.example.chatter.features.calls.data.data_source.remote.UserService
import io.getstream.android.push.firebase.FirebasePushDeviceGenerator
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.notifications.NotificationConfig
import io.getstream.video.android.core.socket.common.token.TokenProvider
import io.getstream.video.android.core.sounds.enableRingingCallVibrationConfig
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.datastore.model.StreamUserPreferences
import io.getstream.video.android.model.User
import io.getstream.video.android.model.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull


/**
 * Alright bro, let’s break this **step by step**, so everything in your `StreamVideoInitHelper` makes sense. I’ll explain it in **simple language** and also clarify why each piece exists. 😎
 *
 * ---
 *
 * ### **1️⃣ The `InitializedState` enum**
 *
 * ```kotlin
 * enum class InitializedState {
 *     NOT_STARTED, RUNNING, FINISHED, FAILED
 * }
 * ```
 *
 * * This is just a way to **track the status** of your Stream Video SDK initialization.
 * * Think of it like a **traffic light**:
 *
 *   * `NOT_STARTED` → Nothing has started yet.
 *   * `RUNNING` → The SDK is currently being initialized.
 *   * `FINISHED` → Initialization completed successfully.
 *   * `FAILED` → Something went wrong while initializing.
 *
 * ---
 *
 * ### **2️⃣ The `StreamVideoInitHelper` object**
 *
 * ```kotlin
 * object StreamVideoInitHelper { ... }
 * ```
 *
 * * `object` in Kotlin → **Singleton** (only one instance in the whole app).
 * * This is like a **helper manager** for initializing the Stream Video SDK.
 * * It keeps track of initialization state, prevents double initialization, and handles errors.
 *
 * ---
 *
 * ### **3️⃣ Variables inside the helper**
 *
 * ```kotlin
 * private var isInitialising = false
 * private val _initState = MutableStateFlow<InitializedState>(InitializedState.NOT_STARTED)
 * public val initializedState: StateFlow<InitializedState> = _initState
 * private var testNotificationConfig: NotificationConfig? = null
 * ```
 *
 * * `isInitialising`: Prevents multiple calls to initialize the SDK at the same time.
 * * `_initState` & `initializedState`:
 *
 *   * `_initState` is **mutable** internally, but external classes only see **read-only** `initializedState`.
 *   * This is used to **observe the SDK state** from UI or other parts of your app using **coroutines Flow**.
 * * `testNotificationConfig`: Optional notification configuration you can pass if you want to **customize push notifications**.
 *
 * ---
 *
 * ### **4️⃣ The `init()` function**
 *
 * ```kotlin
 * fun init(appContext: Context, testNotificationConfig: NotificationConfig? = null) {
 *     this.testNotificationConfig = testNotificationConfig
 *     println("\uD83D\uDE3E  StreamVideoInitHelper init()")
 * }
 * ```
 *
 * * This just **stores the notification config** if you want custom push notifications.
 * * Doesn't actually initialize the SDK — that happens in `loadSdk`.
 *
 * ---
 *
 * ### **5️⃣ The `loadSdk()` suspend function**
 *
 * ```kotlin
 * suspend fun loadSdk(
 *     dataStore: StreamUserDataStore,
 *     context : Context
 * )
 * ```
 *
 * This is the **main function** that initializes Stream Video SDK. Let’s go **step by step inside it**.
 *
 * ---
 *
 * #### **Step A: Check if already installed**
 *
 * ```kotlin
 * if (StreamVideo.isInstalled) {
 *    _initState.value = InitializedState.FINISHED
 *    return
 * }
 * ```
 *
 * * `StreamVideo.isInstalled` → Checks if SDK is already initialized.
 * * If yes, mark `FINISHED` and **don’t reinitialize**.
 *
 * ---
 *
 * #### **Step B: Prevent double initialization**
 *
 * ```kotlin
 * if (isInitialising) {
 *     _initState.value = InitializedState.RUNNING
 *     return
 * }
 * ```
 *
 * * If someone calls `loadSdk` while initialization is **still running**, just return.
 * * This avoids crashes or multiple SDK instances.
 *
 * ---
 *
 * #### **Step C: Start initialization**
 *
 * ```kotlin
 * isInitialising = true
 * _initState.value = InitializedState.RUNNING
 * ```
 *
 * * Mark that initialization **started**.
 *
 * ---
 *
 * #### **Step D: Get user data from `StreamUserDataStore`**
 *
 * ```kotlin
 * val loggedInUser = dataStore.data.firstOrNull()?.user ?: throw ChatterVideoInitFailedException("user is null")
 * val apiKey = dataStore.apiKey.firstOrNull() ?: throw ChatterVideoInitFailedException("Api key is null")
 * val token = dataStore.userToken.firstOrNull() ?: throw ChatterVideoInitFailedException("Token is null")
 * ```
 *
 * * Pull **current user info, API key, and token** from your app’s data store.
 * * If any of these are **missing**, throw an exception and fail initialization.
 * * Basically, **you cannot initialize the SDK without these credentials**.
 *
 * ---
 *
 * #### **Step E: Call the actual SDK initializer**
 *
 * ```kotlin
 * initializeStreamVideo(
 *     context = context,
 *     apiKey = apiKey,
 *     user = loggedInUser,
 *     token = token,
 * )
 * ```
 *
 * * This function does the real work: builds and initializes the **Stream Video SDK instance**.
 *
 * ---
 *
 * #### **Step F: Handle success and failure**
 *
 * ```kotlin
 * _initState.value = InitializedState.FINISHED
 * isInitialising = false
 * ```
 *
 * * If everything goes well → **mark as finished**.
 * * If something goes wrong (catch block) → **mark as failed**.
 * * `isInitialising = false` → ready for **retry if needed**.
 *
 * ---
 *
 * ### **6️⃣ The `initializeStreamVideo()` function**
 *
 * ```kotlin
 * private fun initializeStreamVideo(
 *     context: Context,
 *     apiKey: String,
 *     user: User,
 *     token: String
 * ) : StreamVideo { ... }
 * ```
 *
 * * Creates a **StreamVideo instance** using `StreamVideoBuilder`.
 * * Sets up:
 *
 *   * `context` → Android app context
 *   * `apiKey` → Your Stream account API key
 *   * `user` → Logged-in user object
 *   * `token` → Auth token for SDK
 *   * `notificationConfig` → Push notifications setup
 *   * `callUpdatesAfterLeave = true` → Receive call updates even after leaving
 *   * `appName` → App name for notifications
 *
 * Think of this as **turning on Stream Video for your app** with all the necessary credentials and configs.
 *
 * ---
 *
 * ### ✅ **Summary / Concept Flow**
 *
 * 1. **Singleton helper** manages initialization.
 * 2. **Tracks state** using `StateFlow` (`NOT_STARTED`, `RUNNING`, `FINISHED`, `FAILED`).
 * 3. `loadSdk()`:
 *
 *    * Prevents double initialization
 *    * Fetches user credentials
 *    * Initializes the SDK
 *    * Updates state accordingly
 * 4. `initializeStreamVideo()` → actually builds the SDK instance with all configs.
 * 5. Optional **custom notifications** can be passed via `init()`.
 *
 * ---
 *
 * ### 💡 **Why use it this way?**
 *
 * * Prevents multiple SDK instances → **stable app**
 * * Tracks initialization → **UI can react** (loading spinner, error messages)
 * * Handles missing user/token → **fails gracefully**
 * * Supports **custom notifications** → app can control push behavior
 *
 * ---
 */

enum class InitializedState {
    NOT_STARTED, RUNNING, FINISHED, FAILED
}


class  ChatterVideoInitHelper(
    private val userService: UserService ,
    private val userDataSource: UserDataSource ,
    private val callDevice: CallDevice,
    private val context: Context
    ) {

    private var isInitialising = false
    private val _initState = MutableStateFlow(InitializedState.NOT_STARTED)


    private var testNotificationConfig: NotificationConfig? = null


    fun init(appContext: Context,testNotificationConfig: NotificationConfig? = null) {
        this.testNotificationConfig = testNotificationConfig
    }

    fun reloadSdk(
        data : StreamUserPreferences,
        useRandomUserAsFallback: Boolean = true,
    ) {
        StreamVideo.removeClient()
        loadSdk(
            data,
        )
    }

     fun loadSdk(
        data : StreamUserPreferences ,
    ) {
        if (StreamVideo.isInstalled) {
           _initState.value = InitializedState.FINISHED
            println("\uD83D\uDE3E  StreamVideoInitHelper loadSdk , streamVideo isInstalled initState : ${_initState.value.toString()}")
            return
        }


        if (isInitialising) {
            _initState.value =InitializedState.RUNNING
            println("\uD83D\uDE3E  StreamVideoInitHelper loadSdk , isInitialising initState : ${_initState.value.toString()}")
            return
        }

         isInitialising = true
        _initState.value = InitializedState.RUNNING

        try {
            initializeStreamVideo(
                apiKey = data.apiKey ,
                user = data.user!!,
                token = data.userToken,
            )
            println("\uD83D\uDE3E StreamVideoInitHelper Init successful.")
            _initState.value = InitializedState.FINISHED
        }
        catch (e: Exception) {
            println("\uD83D\uDE3E  StreamVideoInitHelper loadSdk catch : ${e.toString()}")
          _initState.value = InitializedState.FAILED
        }
        isInitialising = false
    }

    private fun initializeStreamVideo(
        apiKey : String ,
        user : User ,
        token : String ,
    ) : StreamVideo{
        return StreamVideoBuilder(
            context = context ,
            apiKey = apiKey ,
            geo = GEO.GlobalEdgeNetwork,
            token = token ,
            user =  user,
            notificationConfig =  testNotificationConfig ?: NotificationConfig(
                pushDeviceGenerators = listOf(
                    FirebasePushDeviceGenerator(
                        context = context,
                        providerName = Constants.PUSH_PROVIDER_NAME,
                    ),
                )),
            appName = "CHATTER APP",
            connectionTimeoutInMs = 12_000L,
            vibrationConfig = enableRingingCallVibrationConfig(),
            tokenProvider = object  : TokenProvider{
                override suspend fun loadToken(): String {
                    println("🚨 Stream loadToken() called")

                    val user = userDataSource.user.firstOrNull()

                    if (user == null) {
                        println("❌ No user available yet!")
                        return ""
                    }

                    val result = userService.loadToken(user.phoneNumber)

                    return result.getOrElse {
                        println("❌ Error fetching token $it")
                        ""
                    }.also { token ->
                        println("✅ Token fetched: $token")
                        if (token.isNotBlank()) {
                            StreamUserDataStore.instance().updateUserToken(token)
                        }
                    }
                }
            }
            ).build()
    }

   suspend fun logoutStreamUser() {
        // Disconnect any active calls, if needed
        if(StreamVideo.isInstalled){
            StreamUserDataStore.instance().userDevice.firstOrNull()?.id?.let{
                callDevice.deleteDevice(it)
                StreamVideo.instance().logOut()
                StreamVideo.removeClient()
            }
        }
    }


    suspend fun initChatterVideo(
        phoneNumber: PhoneNumber ,
                                 localToken : String){
        val isInserted =  userService.saveUser(
            createUserRequest = CreateUserRequest(
                phoneNumber = phoneNumber,
            )
        ).getOrNull() ?: return
        if(isInserted){
            println("\uD83D\uDE1B ChatterVideoInitHelper setUpChatterApp  call user have saved ")
            val user = userService.getUser(phoneNumber).getOrNull()
            if (user != null) {
                println("\uD83D\uDE1B ChatterVideoInitHelper setUpChatterApp  user is $user ")
                val streamUserDataStore = StreamUserDataStore.instance()
                streamUserDataStore.updateUserPreferences(
                    streamUserPreferences = StreamUserPreferences(
                        user = User(
                            id = user.phoneNumber.toString(),
                            name = user.name,
                            type = UserType.Authenticated,
                            role = "admin" ,
                        ),
                        apiKey = Constants.API_KEY,
                        userToken = user.token,
                    )
                )
                streamUserDataStore.data.firstOrNull()?.let {
                    println("\uD83D\uDE1B ChatterVideoInitHelper setUpChatterApp streamUserDataStore , data , ${it.toString()}")
                    loadSdk(
                        data = it,
                    )
                    val device = callDevice.createDevice(localToken)
                    println("\uD83D\uDE1B ChatterVideoInitHelper setUpChatterApp   device : $device")
                    streamUserDataStore.updateUserPreferences(it.copy(userDevice = device))
                }
            }
        }
    }
}