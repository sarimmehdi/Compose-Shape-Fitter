package com.sarim.composeshapefittersampleapp.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object SnackBarController {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}

data class SnackbarEvent(
    val message: UiText,
    val action: SnackbarAction? = null,
)

data class SnackbarAction(
    val name: UiText,
    val action: suspend () -> Unit = {},
)