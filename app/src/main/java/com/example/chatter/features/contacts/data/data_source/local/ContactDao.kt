package com.example.chatter.features.contacts.data.data_source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import com.example.chatter.core.data.data_source.local.BaseDao
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactDao : BaseDao<ContactEntity>{

    @Transaction
    @Query("SELECT * FROM contacts")
    fun getAllContacts() : Flow<List<ContactEntity>>

    @Query("""
        SELECT * FROM contacts
        WHERE _phoneNo_countryCode = :countryCode 
        AND _phoneNo_phoneNumber = :phoneNumber
    """)
    suspend fun getContactByPhoneNumber(
        countryCode: String,
        phoneNumber: String
    ): ContactEntity?



    @Query("DELETE FROM contacts WHERE userId = :userId AND _phoneNo_countryCode = :countryCode AND _phoneNo_phoneNumber =:phoneNumber")
    suspend fun deleteContact(
        userId : String ,
        countryCode : String ,
        phoneNumber : String
    )



    @Query("DELETE FROM contacts")
    suspend fun clear()

    @Query(
        "UPDATE contacts SET lastSeen = :lastSeen WHERE _phoneNo_phoneNumber = :number AND _phoneNo_countryCode = :countryCode"
    )
    suspend fun updateLastSeenByPhoneNumber(
        countryCode: String,
        number: String,
        lastSeen: Long
    ): Int



    @Query(
        "UPDATE contacts SET lastSeen = :lastSeen , image  = :image " +
                "WHERE _phoneNo_phoneNumber = :number " +
                "AND _phoneNo_countryCode = :countryCode"
    )
    suspend fun updateLastSeenAndImageByPhoneNumber(
        countryCode: String,
        number: String,
        lastSeen: Long ,
        image : String?
    ): Int



    @Query("""
        SELECT * FROM contacts
        WHERE _phoneNo_countryCode = :countryCode 
        AND _phoneNo_phoneNumber = :phoneNumber
    """)
     fun getContact(
        countryCode: String,
        phoneNumber: String
    ): Flow<List<ContactEntity>>
}