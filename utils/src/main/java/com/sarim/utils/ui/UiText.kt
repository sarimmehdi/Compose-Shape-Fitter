package com.sarim.utils.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(
        val value: String,
    ) : UiText()

    class StringResource(
        @param:StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    @Suppress("SpreadOperator")
    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(this.resId, *this.args)
        }

    @Suppress("SpreadOperator")
    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(this.resId, *this.args)
        }
}
