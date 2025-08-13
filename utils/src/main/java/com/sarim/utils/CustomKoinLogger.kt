package com.sarim.utils

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class CustomKoinLogger(
    private val tag: String,
    level: Level = Level.INFO
) : Logger(level) {

    override fun display(level: Level, msg: MESSAGE) {
        if (this.level <= level) {
            when (level) {
                Level.DEBUG -> log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.DEBUG,
                    shouldLog = true
                )
                Level.INFO -> log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.INFO,
                    shouldLog = true
                )
                Level.WARNING -> log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.WARN,
                    shouldLog = true
                )
                Level.ERROR -> log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.ERROR,
                    shouldLog = true
                )
                Level.NONE -> { }
            }
        }
    }
}
