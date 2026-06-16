package com.example.chatter.features.contacts.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.features.contacts.presentation.create_contact.CreateContactScreen
import com.example.chatter.features.contacts.presentation.create_contact.CreateContactViewModel
import com.example.chatter.features.contacts.presentation.util.ContactScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.contactsNavigation(
    navController: NavController ,
    startDestination : Any ,
    onSaveContact : () -> Unit
){
    navigation<Graph.Contacts>(
        startDestination = startDestination
    ){
        composable<ContactScreen.CreateContact> {
            val vm = koinViewModel<CreateContactViewModel>()
            CreateContactScreen(
                viewModel = vm,
                onSave = onSaveContact
            )
        }
    }
}