package com.sarim.compose_shape_fitter.utils

import android.util.Log
import com.sarim.compose_shape_fitter.BuildConfig

enum class LogType {
    WARN, DEBUG, INFO, ERROR;
}

inline fun log(tag: String, messageBuilder: () -> String, logType: LogType, logRegardless: Boolean = false) {
    if (BuildConfig.DEBUG || logRegardless) {
        when(logType) {
            LogType.WARN -> Log.w(tag, messageBuilder())
            LogType.DEBUG -> Log.d(tag, messageBuilder())
            LogType.INFO -> Log.i(tag, messageBuilder())
            LogType.ERROR -> Log.e(tag, messageBuilder())
        }
    }
}