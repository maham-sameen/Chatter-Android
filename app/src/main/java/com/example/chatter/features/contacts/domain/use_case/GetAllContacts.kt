package com.example.chatter.features.contacts.domain.use_case

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import com.example.chatter.features.contacts.presentation.model.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllContacts (
    private val contactRepo: ContactRepo ,
    private val fromHexCode: FromHexCode
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun  invoke(): Flow<SnapshotStateList<Contact>>{
        return contactRepo.getAllContacts()
            .mapLatest { contacts ->
                println("\uD83E\uDD2D contacts : ${contacts.toString()}")
                mutableStateListOf<Contact>().apply {
                    addAll(
                        contacts.map {
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
                    )
                }
            }
    }
}
