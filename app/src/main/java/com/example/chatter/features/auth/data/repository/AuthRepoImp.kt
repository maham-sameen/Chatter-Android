package com.example.chatter.features.auth.data.repository

import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.MemberEntity
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.core.data.data_source.remote.UsersDataStore
import com.example.chatter.core.domain.model.User
import com.example.chatter.features.auth.data.data_source.model.mapper.CCSigninToCCSigninReq
import com.example.chatter.features.auth.data.data_source.model.mapper.CCSignupToCCSignupReq
import com.example.chatter.features.auth.data.data_source.model.mapper.ChatRoomResponseToChatRoomEntity
import com.example.chatter.features.auth.data.data_source.model.mapper.SCSigninToSCSigninReq
import com.example.chatter.features.auth.data.data_source.model.mapper.SCSignupToSCSignupReq
import com.example.chatter.features.auth.data.data_source.model.remote.AuthResponse
import com.example.chatter.features.auth.data.data_source.model.remote.AuthResponseToUserPreference
import com.example.chatter.features.auth.data.data_source.model.remote.GetUserDataResponse
import com.example.chatter.features.auth.data.data_source.remote.AuthenticationService
import com.example.chatter.features.auth.data.data_source.remote.SetUpService
import com.example.chatter.features.auth.domain.model.CheckCodeSignin
import com.example.chatter.features.auth.domain.model.CheckCodeSignup
import com.example.chatter.features.auth.domain.model.SendCodeSignin
import com.example.chatter.features.auth.domain.model.SendCodeSignup
import com.example.chatter.features.auth.domain.repository.AuthRepo
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import com.example.chatter.features.notifications.data.data_source.model.remote.FcmTokenRequest
import com.example.chatter.features.notifications.data.data_source.remote.TokenService
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json


class AuthRepoImp(
    private val client : HttpClient,
    private val scSignupToSCSignupReq: SCSignupToSCSignupReq,
    private val ccSignupToCCSignupReq: CCSignupToCCSignupReq,
    private val scSigninToSCSigninReq: SCSigninToSCSigninReq,
    private val ccSigninToCCSigninReq: CCSigninToCCSigninReq,
    private val chatRoomResponseToChatRoomEntity: ChatRoomResponseToChatRoomEntity,
    private val userDataSource: UserDataSource,
    private val authResponseToUserPreference: AuthResponseToUserPreference,
    private val usersDataStore: UsersDataStore,
    private val chatRoomDao: ChatRoomDao,
    private val memberDao: MemberDao,
    private val contactDao : ContactDao,
    private val userSocketService: PresenceSocketService,
    private val chatterVideoInitHelper: ChatterVideoInitHelper ,
)  : AuthRepo {
    override suspend fun sendCodeForSignUp(sendCodeSignUp: SendCodeSignup): Result<Unit> {
        println("\uD83D\uDE1B AuthRepoImp sendCodeForSignUp")
        return runCatching {
            val response = client.post {
                url(AuthenticationService.EndPoints.SignupSendCode.url)
                setBody(scSignupToSCSignupReq.map(sendCodeSignUp))
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp , response ${response.toString()}")
            if (response.status == HttpStatusCode.OK) Unit
            else throw Exception(body)
        }
    }


    override suspend fun sendCodeForSignIn(sendCodeSignIn: SendCodeSignin): Result<Unit> {
        println("\uD83D\uDE1B AuthRepoImp sendCodeForSignIn")
        return runCatching {
            val response = client.post {
                url(AuthenticationService.EndPoints.SigninSendCode.url)
                setBody(scSigninToSCSigninReq.map(sendCodeSignIn))
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp sendCodeForSignin body $body")
            when (response.status) {
                HttpStatusCode.OK -> Unit
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }


    override suspend fun checkCodeForSignUp(checkCodeSignUp: CheckCodeSignup): Result<Unit> {
        println("\uD83D\uDE1B AuthRepoImp checkCodeForSignUp")
        return runCatching {
            val response = client.post {
                url(AuthenticationService.EndPoints.SignupCheckCode.url)
                setBody(ccSignupToCCSignupReq.map(checkCodeSignUp))
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp checkCodeForSignUp body $body")
            when (response.status) {
                HttpStatusCode.OK -> Unit
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    /**
     *  when user cookie is sent and the user sign in again without close the
     *  app then app still use the same cookie and then
     *  we need to tell the system that  you need to restart the app
     *  something but we are leaving that .
     */
    override suspend fun checkCodeForSignIn(checkCodeSignIn: CheckCodeSignin): Result<Unit> {
        println("\uD83D\uDE1B AuthRepoImp checkCodeForSignin")
        return runCatching {
            val response = client.post {
                url(AuthenticationService.EndPoints.SigninCheckCode.url)
                setBody(ccSigninToCCSigninReq.map(checkCodeSignIn))
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp checkCodeForSignin body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = Json.decodeFromString<AuthResponse>(body)
                    // save jwt
                    userSocketService.closeSession()
                    userDataSource.clear()
                    chatRoomDao.clear()
                    memberDao.clear()
                    contactDao.clear()
                    chatterVideoInitHelper.logoutStreamUser()
                    userDataSource.updateUserPreferences(authResponseToUserPreference.map(authResponse))
                    setUpChatterApp()
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }







    /**
     * while inserting the member we do not insert the member that
     * is itself is the user phone number because we already know
     * that the user is the member of the phone number .
     */
    private suspend fun setUpChatterApp(){
        println("\uD83D\uDE1B AuthRepoImp setUpChatterApp ")
        userDataSource.user.firstOrNull()?.let{  user ->
            println("\uD83D\uDE1B AuthRepoImp setUpChatterApp  , phoneNumber : ${user.phoneNumber}")
            val response = client.post {
                url(SetUpService.EndPoints.GetUserRelatedData.url)
                setBody(user.phoneNumber)
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp setUpChatterApp  body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val getUserData = Json.decodeFromString<GetUserDataResponse>(body)
                    getUserData.contacts.forEach {
                        val userId = user.phoneNumber.toString()
                        val isUser =
                           it.phoneNumber.toString() == userId
                        contactDao.upsert(
                            entity = ContactEntity(
                                userId = userId,
                                name = it.name,
                                phoneNumber = it.phoneNumber,
                                isUser = isUser,
                                image = it.image,
                                colorScheme = it.colorScheme,
                                lastSeen = it.lastSeen
                            )
                        )
                    }
                    getUserData.chatRooms.forEach {
                        chatRoomDao.upsert(
                            chatRoomResponseToChatRoomEntity.map(it)
                        )
                    }
                    getUserData.members.forEach {
                        if(it.phoneNumber != user.phoneNumber){
                            memberDao.upsert(
                                MemberEntity(
                                    roomId = it.roomId,
                                    phoneNumber = it.phoneNumber
                                )
                            )
                        }
                    }
                    userSocketService.initSession(
                        UserSession(
                            userPhone = user.phoneNumber.toString(),
                            contactPhone = listOf()
                        ),
                        scope = CoroutineScope(Dispatchers.IO)
                    )
                    val localToken = Firebase.messaging.token.await()
                    saveFcmToken(
                        phoneNumber = user.phoneNumber,
                        token = localToken
                    ).fold(
                        onSuccess = { Unit},
                        onFailure = {it}
                    )
                    chatterVideoInitHelper.initChatterVideo(
                        phoneNumber = user.phoneNumber,
                        localToken = localToken,
                    )
                }
                HttpStatusCode.BadRequest -> throw Exception(body)
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        println("\uD83D\uDE1B AuthRepoImp getAllUsers")
        return usersDataStore.getAllUsers()
    }

    private suspend fun saveFcmToken(
        phoneNumber : PhoneNumber ,
        token : String
    ) : Result<Unit> {
        println("\uD83C\uDFAA AuthRepoImp saveFcmToken  ,  phoneNumber  : $phoneNumber , token : $token")
        return runCatching {
            val response = client.post {
                url(TokenService.EndPoints.SaveToken.url)
                setBody(
                    FcmTokenRequest(
                        phoneNumber = phoneNumber,
                        fcmToken = token
                    )
                )
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B AuthRepoImp saveFcmToken : body : $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val isSuccess = Json.decodeFromString<Boolean>(body)
                    if (isSuccess) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Notification can `t be activated due to some errors"))
                    }
                }

                HttpStatusCode.BadRequest -> throw Exception("Bad request")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }
}