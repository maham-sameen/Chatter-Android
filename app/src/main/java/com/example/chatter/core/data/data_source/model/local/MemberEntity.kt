package com.example.chatter.core.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName


@Entity(
    tableName = "members" ,
    foreignKeys = [
        ForeignKey(
            entity = ChatRoomEntity::class,
            parentColumns = ["roomId"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    @SerialName("_id")
    val _id : Long = 0 ,
    val roomId : String ,
    @Embedded(prefix = "_phoneNo_")
    val phoneNumber : PhoneNumber
)