package com.example.chatter.core.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.chatter.AppDatabase
import com.example.chatter.AppLifecycleObserver
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.mapper.UserDtoToUser
import com.example.chatter.core.data.data_source.remote.ChatterSocket
import com.example.chatter.core.data.data_source.remote.GetDownloadUrl
import com.example.chatter.core.data.data_source.remote.UserService
import com.example.chatter.core.data.data_source.remote.UserServiceImp
import com.example.chatter.core.data.data_source.remote.UsersDataStore
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.chats.domain.repository.ChatRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val  coreAppModule  = module{

    single<AppDatabase>{
        Room.databaseBuilder(
            context = get<Context>(),
            klass = AppDatabase::class.java ,
            name = "chatter_database"
        ).build()
    }

    single { WorkManager.getInstance(androidContext()) }

    single<UserService> {
        UserServiceImp(
            client = get<HttpClient>()
        )
    }

    single<Json> {
        Json {
            ignoreUnknownKeys = true      // ignore unknown fields in response
            prettyPrint = true            // (optional) makes debug logs readable
            isLenient = true              // (optional) allows non-strict JSON
        }
    }

    single<HttpClient> {
        HttpClient(CIO){
            install(Logging)
            install(ContentNegotiation){
                json(get<Json>())
            }
            install(HttpCookies)
            install(WebSockets) {
                pingInterval = 15.seconds // ✅ send ping every 15 seconds
            }
        }
    }

    single<UserDataSource>{
        UserDataSource.instance()
    }

    single<UsersDataStore> {
        UsersDataStore(
            client = get<HttpClient>(),
            userDtoToUser = UserDtoToUser()
        )
    }

    single<MemberDao>{
        get<AppDatabase>().memberDao()
    }

    single<ChatRoomDao>{
        get<AppDatabase>().chatRoomDao()
    }

    single<LocalImageStorage> {
        LocalImageStorage(
            androidContext()
        )
    }

    single<GetDownloadUrl>{
        GetDownloadUrl()
    }
}