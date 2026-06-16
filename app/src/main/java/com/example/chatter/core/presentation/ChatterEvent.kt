package com.example.chatter.core.presentation

sealed class ChatterEvent {
    object OnDismiss : ChatterEvent()
    object InitSession : ChatterEvent()
    object CloseSession : ChatterEvent()
}