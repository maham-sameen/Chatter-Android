package com.example.chatter.core.presentation.utils

import androidx.navigation.NavHostController


fun navigate(
    navController : NavHostController ,
    route : Any
){
    navController.navigate(route) {
        popUpTo(route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}