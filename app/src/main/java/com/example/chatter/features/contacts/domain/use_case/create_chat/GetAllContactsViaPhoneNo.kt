package com.example.chatter.features.contacts.domain.use_case.create_chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import com.example.chatter.features.contacts.presentation.model.Contact
import kotlinx.coroutines.flow.Flow

class GetAllContactsViaPhoneNo(
    private val contactRepo: ContactRepo ,
    private val fromHexCode: FromHexCode
) {

    suspend operator fun invoke(
        list : List<PhoneNumber>
    ) : SnapshotStateList<Contact>{
        println("\uD83E\uDEAD GetAllContactsViaPhoneNumber  , list : ${list.toString()}")
        return mutableStateListOf<Contact>().apply {
            addAll(
                list.mapNotNull { phoneNumber ->
                    println("\uD83E\uDEAD GetAllContactsViaPhoneNumber  , phoneNumber : ${phoneNumber.toString()}")
                    contactRepo
                        .getContactViaPhoneNumber(
                            countryCode = phoneNumber.countryCode,
                            phoneNumber = phoneNumber.phoneNumber
                        )?.let {
                            println("\uD83E\uDEAD GetAllContactsViaPhoneNumber  , contact : ${it.toString()}")
                            Contact(
                                name = it.name,
                                color = fromHexCode(it.colorScheme!!),
                                phoneNumber = PhoneNumber(
                                    countryCode = it.phoneNumber.countryCode,
                                    phoneNumber = it.phoneNumber.phoneNumber
                                ),
                                image = it.image,
                                isSelected = false
                            )
                        }
                }
            )
        }
    }
}