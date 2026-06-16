package com.example.chatter.features.contacts.domain.use_case.create_chat

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.core.domain.use_case.ToHexCode
import com.example.chatter.features.contacts.domain.model.ChatRoom
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import com.example.chatter.features.contacts.presentation.model.Contact
import org.apache.commons.codec.digest.DigestUtils



class InsertChatRoom (
    private val contactRepo: ContactRepo ,
    private val toHexCode: ToHexCode ,
    private val getRandomColor: GetRandomColor
){

    /**
     * here when we insert the chat room in the server then we
     * pass our user phone number but when we insert the
     * chat room in the app database then we do not pass the user phone number
     *  because we already know user itself is the member of the chat room .
     *  so we do not insert the user as the member of that chat room
     *
     *
     *  now if we have one to one chat then chat room name , image and color scheme will be null
     */
    suspend operator fun invoke(
         name : String ,
         image : ByteArray? ,
         contacts : SnapshotStateList<Contact> ,
    ): Result<Unit>{
        println("\uD83E\uDD65 InsertChatRoom , name : $name  , contacts : ${contacts.toString()}")
        val downloadUrl = image?.let {
            contactRepo.uploadChatRoomImage(it).getOrNull()
        }
        println("\uD83E\uDD65 InsertChatRoom , downloadUrl : $downloadUrl")
        val members = mutableListOf<PhoneNumber>()
        members.addAll(contacts.map {
            PhoneNumber(
                countryCode = it.phoneNumber.countryCode,
                phoneNumber = it.phoneNumber.phoneNumber
            )
        })
        val currentUserPhoneNumber = PhoneNumber.fromString(contactRepo.getCurrentUserId())
        members.add(currentUserPhoneNumber)

        val phoneNumbers = members
            .map { it ->  it.toString() }
        val roomId = generateRoomId(phones = phoneNumbers)

        println("\uD83E\uDD65 InsertChatRoom members : ${members.toString()} , roomId : $roomId")
        val chatRoom = ChatRoom(
            roomId = "",
            name =  name,
            image = downloadUrl,
            colorScheme = if(contacts.size == 1){
                // means one to one chat
                ColorScheme("" , "")
            } else {toHexCode(getRandomColor())},
            timeStamp = 0,
            members = members
        )

        return contactRepo.saveChatRoom(
            chatRoom = chatRoom
        ).fold(
            onSuccess = {
                // from server we got nothing about roomId and members
                println("\uD83E\uDD65 InsertChatRoom , onSuccess : ${it.toString()}")
                contactRepo.insertChatRoom(
                    chatRoom = ChatRoom(
                        roomId = roomId,
                        name = it.name,
                        image = it.image,
                        colorScheme = it.colorScheme,
                        members = members,
                        timeStamp = it.timeStamp
                    ),
                    phoneNumber = currentUserPhoneNumber
                )
            },
            onFailure = {
                println("\uD83E\uDD65 InsertChatRoom , onFailure : ${it.toString()}")
                Result.failure(it)
            }
        )
    }

   private fun generateRoomId(
        phones : List<String>
    ): String {
        println("\uD83E\uDEE0 generateRoomId ${phones.toString()}")
        return DigestUtils.sha256Hex(phones.sorted().joinToString("_"))
    }
}