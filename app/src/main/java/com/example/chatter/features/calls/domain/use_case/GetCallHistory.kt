package com.example.chatter.features.calls.domain.use_case

import androidx.paging.PagingData
import androidx.paging.map
import com.example.chatter.R
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.calls.domain.model.CallStatus
import com.example.chatter.features.calls.domain.repository.CallRepo
import com.example.chatter.features.calls.presentation.call_history.model.Call
import com.example.chatter.core.domain.use_case.FormateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetCallHistory(
    private val callRepo: CallRepo ,
    private val getRandomColor: GetRandomColor ,
    private val formateTime: FormateTime
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId : String) : Flow<PagingData<Call>>{
        println("⏳ GetCallHistory invoke() ")
        return callRepo.getCallHistory(userId)
            .mapLatest {pagingData ->
                println("\uD83E\uDD5D [GetCallHistory]  , pagingData : ${pagingData.toString()}")
                pagingData.map { call ->
                    println("\uD83E\uDD5D [GetCallHistory]  , call : ${call.toString()}")
                    val (image , name)  = if(call.members.size == 2){
                        val member =  call.members.find { it.id != userId }
                        member?.image to member?.name
                    }else {null to null}
                    val parts = call.cid.split(":")
                    val icon = when(parts[0]){
                        "default" -> {
                            when(call.status){
                                CallStatus.Missed -> {
                                    R.drawable.video__1_
                                }
                                CallStatus.Incoming -> {
                                    R.drawable.video__1_
                                }
                                CallStatus.Outgoing -> {
                                    R.drawable.video
                                }
                            }
                        }
                        "audio_room" -> {
                            when(call.status){
                                CallStatus.Missed -> {
                                    R.drawable.rejected
                                }
                                CallStatus.Incoming -> {
                                    R.drawable.phone
                                }
                                CallStatus.Outgoing -> {
                                    R.drawable.call_out
                                }
                            }
                        }
                        else -> { 0 }
                    }
                    println("\uD83E\uDD5D [GetCallHistory]  , image : $image , name : $name , icon : $icon")
                    Call(
                        callId = parts[1],
                        members = call.members.map { it.id },
                        image = image,
                        colorScheme = getRandomColor(),
                        name = name ?: "${call.members.first().name}&..",
                        status = call.status.name,
                        isVideo = if(parts[0] == "default") true else false,
                        timeStamp = formateTime(call.timeStamp),
                        statusIcon = icon
                    )
                }
            }
    }
}