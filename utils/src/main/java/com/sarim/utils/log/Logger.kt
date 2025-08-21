package com.sarim.utils.log

import android.util.Log

inline fun log(
    tag: String,
    messageBuilder: () -> String,
    logType: LogType,
    shouldLog: Boolean,
) {
    if (shouldLog) {
        when (logType) {
            LogType.WARN -> Log.w(tag, messageBuilder())
            LogType.DEBUG -> Log.d(tag, messageBuilder())
            LogType.INFO -> Log.i(tag, messageBuilder())
            LogType.ERROR -> Log.e(tag, messageBuilder())
        }
    }
}

enum class LogType {
    WARN,
    DEBUG,
    INFO,
    ERROR,
}
