package com.example.chatter.features.contacts.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber

@Entity(
    tableName = "contacts"
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val contactId : Long = 0,
    // +92-3276010123
    val userId: String,
    val name : String,
    @Embedded(prefix = "_phoneNo_")
    val phoneNumber : PhoneNumber,
    val isUser : Boolean = false,

    val image : String? = null,
    @Embedded("_color")
    val colorScheme : ColorScheme? = null ,
    val lastSeen : Long? = null
)