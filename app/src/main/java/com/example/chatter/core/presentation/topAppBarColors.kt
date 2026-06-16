package com.example.chatter.core.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarColors(
    containerColor : Color = MaterialTheme.colorScheme.surfaceContainer
) = TopAppBarDefaults.topAppBarColors(
    titleContentColor = MaterialTheme.colorScheme.onSurface ,
    containerColor =  containerColor,
    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.onSurface
)