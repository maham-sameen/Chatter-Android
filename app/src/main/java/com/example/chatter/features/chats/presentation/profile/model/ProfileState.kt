package com.example.chatter.features.chats.presentation.profile.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.chatter.ui.ColorScheme

data class ProfileState (
    val image: String? = null,
    val colorScheme: ColorScheme,
    val userDetails : SnapshotStateList<UserDetail> = mutableStateListOf<UserDetail>()
)