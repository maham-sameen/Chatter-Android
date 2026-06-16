package com.example.chatter.core.data.data_source.local

import androidx.room.Dao
import androidx.room.Query
import com.example.chatter.core.data.data_source.model.local.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao : BaseDao<MemberEntity>{

    @Query("DELETE FROM members")
    suspend fun clear()

    @Query("DELETE FROM members WHERE roomId = :id AND _phoneNo_countryCode = :countryCode AND _phoneNo_phoneNumber =:phoneNumber")
    suspend fun deleteMember(
        id : String ,
        countryCode : String ,
        phoneNumber : String
    )

    @Query("SELECT * FROM members WHERE roomId =:id")
    suspend fun getRoomMembers(id : String): List<MemberEntity>

    @Query("SELECT * FROM members WHERE roomId =:roomId")
    fun getRoomMembersOfRoom(roomId : String): Flow<List<MemberEntity>>
}