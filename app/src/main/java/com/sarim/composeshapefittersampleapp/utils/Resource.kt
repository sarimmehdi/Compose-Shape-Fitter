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
    ) : MessageType {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is IntMessage) return false

            if (message != other.message) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    data class StringMessage(
        val message: String,
    ) : MessageType
}