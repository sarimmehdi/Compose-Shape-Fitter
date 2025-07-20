package com.sarim.composeshapefittersampleapp.utils

sealed interface Resource<T> {
    data class Success<T>(
        val data: T,
    ) : Resource<T>

    data class Error<T>(
        val message: MessageType,
    ) : Resource<T>
}

sealed interface MessageType {
    class IntMessage(
        val message: Int,
        vararg val args: Any,
    ) : MessageType

    data class StringMessage(
        val message: String,
    ) : MessageType
}