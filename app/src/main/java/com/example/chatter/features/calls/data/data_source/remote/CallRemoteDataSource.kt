package com.example.chatter.features.calls.data.data_source.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.chatter.features.calls.data.data_source.model.local.CallMember
import com.example.chatter.features.calls.data.data_source.model.local.CallStatus
import com.example.chatter.features.calls.data.data_source.model.local.ChatterCall
import com.example.chatter.features.calls.data.data_source.model.remote.ChatterCallResult
import io.getstream.android.video.generated.models.CallResponse
import io.getstream.android.video.generated.models.CallSessionResponse
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.internal.ExperimentalStreamVideoApi
import io.getstream.video.android.core.model.CallData
import io.getstream.video.android.core.model.SortField
import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class CallRemoteDataSource (){

    private val client = StreamVideo.instance()
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalStreamVideoApi::class)
    suspend fun getRecentCalls(
        userId : String ,
        limit : Int ,
        prev : String? ,
        next : String?
    ): ChatterCallResult {
        val callHistories = mutableListOf<ChatterCall>()
        println("🔹 [CallRemoteDataSource] Starting getRecentCalls()... , \n  prev $prev , next $next")

        val queryResult = client.queryCalls(
            filters = mapOf(
                "\$or" to listOf(
                    mapOf("members" to mapOf("\$in" to listOf(userId))),
                    mapOf("created_by_user_id" to userId)
                )
            )
            ,
            limit = limit,
            prev = prev,
            next = next,
            sort = listOf(SortField.Desc("starts_at"))
        ).getOrNull()

        if (queryResult == null) {
            println("❌ [CallRemoteDataSource] Query failed or returned null")
            return ChatterCallResult()
        }

        println("✅ [CallRemoteDataSource] Query successful! Total calls: ${queryResult.calls.size}\n")

        // Process queried calls
        queryResult.calls.forEachIndexed { index, call ->
            println("📞 [CallRemoteDataSource] Call #${index + 1} - CID: ${call.call.cid}")
            queryResult.original.calls.find { it.call.cid == call.call.cid }?.call?.let { originalCall ->

                val callHistory = call.toCallHistory(
                    userId = userId,
                    session = originalCall.session,
                    callResponse = originalCall
                )
                println("  ▶ [CallRemoteDataSource] CallEntity: $callHistory")
                println("  ▶ [CallRemoteDataSource]  Members:")
                callHistory.members.forEach { member ->
                    println("    - ${member.name} (ID: ${member.id}, Image: ${member.image})")
                }

                callHistories.add(callHistory)
            }
            println(" [CallRemoteDataSource] ⬇ Members in raw SDK call object:")
            call.members.forEach { member ->
                println(" [CallRemoteDataSource] - ${member.name} (ID: ${member.id}, Image: ${member.imageUrl ?: "N/A"})")
            }
            println("--------------------------------------------------")
        }

        println("🔹 Original calls dump:")
        queryResult.original.calls.forEach { originalCall ->
            println("📌[CallRemoteDataSource] Original Call - CID: ${originalCall.call.cid}")
            println("  [CallRemoteDataSource] Session: ${originalCall.call.session}")
            println("  [CallRemoteDataSource] Members:")
            originalCall.members.forEach { member ->
                println(" [CallRemoteDataSource]    - ${member.user.name} (ID: ${member.user.id}, Image: ${member.user.image ?: "N/A"})")
            }
            println("--------------------------------------------------")
        }

        // Sort the list by timestamp descending
        callHistories.sortByDescending { it.timeStamp }

        println("🔹[CallRemoteDataSource] Finished getRecentCalls(). Total processed calls: ${callHistories.size}")
        return ChatterCallResult(
            chatterCall = callHistories.toList(),
            prev = queryResult.prev,
            next = queryResult.next
        )
    }
    private fun isMissedCall(call: CallResponse): Boolean {

        println("\n📌 [CallRemoteDataSource] Checking missed call:")
        println("➡️[CallRemoteDataSource] Call ID: ${call.cid}")
        println("➡️[CallRemoteDataSource] Call Type: ${call.type}")
        println("➡️[CallRemoteDataSource] User Joined: ${call.session != null}")

        val joined = call.session != null

        val maxRing = call.settings.ring.incomingCallTimeoutMs.toLong()
        println("➡️[CallRemoteDataSource] Max Ring Timeout (ms): $maxRing")

        val callEndTime = call.endedAt ?: OffsetDateTime.now()
        val callDuration = Duration.between(call.createdAt, callEndTime).toMillis()

        println("➡️ [CallRemoteDataSource] Call Duration (ms): $callDuration")
        println("➡️ [CallRemoteDataSource] Condition: inbound=${call.type == "inbound"}, joined=$joined, timeoutReached=${callDuration >= maxRing}")

        val missed = call.type == "inbound" && !joined && callDuration >= maxRing
        println("🛑 [CallRemoteDataSource] Missed Call Detected: $missed\n")

        return missed
    }

    // --- Models for your app ---
    private fun determineStatus(
        session: CallSessionResponse?,
        creatorId: String?,
        userId: String, call : CallResponse
    ): CallStatus {
        return when {
            session?.missedBy?.containsKey(userId) == true  || isMissedCall(call) -> CallStatus.Missed
            creatorId == userId -> CallStatus.Outgoing
            else -> CallStatus.Incoming
        }
    }


    @OptIn(ExperimentalTime::class)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun CallData.toCallHistory(userId: String ,  session: CallSessionResponse?, callResponse : CallResponse): ChatterCall{
        val mappedMembers = members.map {
            CallMember(
                image = it.imageUrl ?: "",
                name = it.name ?: "Unknown",
                id = it.id,
            )
        }

        val status = determineStatus(
            creatorId = call.createdByUserId,
            userId = userId,
            session = session,
            call = callResponse
        )

        return ChatterCall(
            cid = call.cid,
            status = status,
            timeStamp = call.createdAt?.toInstant()?.toEpochMilli() ?: Clock.System.now().toEpochMilliseconds(),
            members = mappedMembers,
        )
    }
}