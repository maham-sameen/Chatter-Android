package com.example.chatter.features.calls.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.calls.data.data_source.model.local.CallStatus
import com.example.chatter.features.calls.data.data_source.model.local.ChatterCall
import com.example.chatter.features.calls.domain.model.CallMember

class ChatterCallDbToChatterCall : Mapper<ChatterCall , com.example.chatter.features.calls.domain.model.ChatterCall> {
    override fun map(from: ChatterCall): com.example.chatter.features.calls.domain.model.ChatterCall {
        return com.example.chatter.features.calls.domain.model.ChatterCall(
            cid = from.cid,
            members = from.members.map {
                CallMember(
                    image = it.image,
                    name = it.name,
                    id = it.id
                )
            },
            status = when(from.status){
                CallStatus.Outgoing -> com.example.chatter.features.calls.domain.model.CallStatus.Outgoing
                CallStatus.Incoming -> com.example.chatter.features.calls.domain.model.CallStatus.Incoming
                CallStatus.Missed -> com.example.chatter.features.calls.domain.model.CallStatus.Missed
            },
            timeStamp = from.timeStamp
        )
    }
}