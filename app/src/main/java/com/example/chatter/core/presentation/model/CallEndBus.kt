package com.example.chatter.core.presentation.model

object CallEndBus {
    val callEnded = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}
