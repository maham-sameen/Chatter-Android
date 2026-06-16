package com.example.chatter.features.calls.data.data_source.remote

import com.example.chatter.Constants
import io.getstream.android.push.PushDevice
import io.getstream.video.android.core.StreamVideo
import io.getstream.android.push.PushProvider
import io.getstream.video.android.model.Device

class CallDevice {

    suspend fun createDevice(fcmToken : String) : Device?{
         return if(StreamVideo.isInstalled){
            with(StreamVideo.instance()){
                println("\uD83C\uDFBE [CallDevice] StreamDevice : createDevice : fcmToken : $fcmToken")
                createDevice(
                    PushDevice(
                        token = fcmToken,
                        pushProvider = PushProvider.fromKey(PushProvider.FIREBASE.key),
                        providerName = Constants.PUSH_PROVIDER_NAME
                    )
                ).getOrNull()
            }
        }else {
             null
         }
    }

    suspend fun deleteDevice(fcmToken : String){
        if(StreamVideo.isInstalled){
            with(StreamVideo.instance()){
                println("\uD83C\uDFBE [CallDevice] StreamDevice : deleteDevice : fcmToken : $fcmToken")
                deleteDevice(
                    Device(
                        id = fcmToken,
                        pushProvider = PushProvider.FIREBASE.key,
                        pushProviderName = Constants.PUSH_PROVIDER_NAME ,
                    )
                )
            }
        }
    }
}