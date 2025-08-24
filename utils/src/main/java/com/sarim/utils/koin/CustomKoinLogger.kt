package com.sarim.utils.koin

import com.sarim.utils.log.LogType
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
                Level.DEBUG -> com.sarim.utils.log.log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.DEBUG,
                    shouldLog = true
                )
                Level.INFO -> com.sarim.utils.log.log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.INFO,
                    shouldLog = true
                )
                Level.WARNING -> com.sarim.utils.log.log(
                    tag = tag,
                    messageBuilder = { msg },
                    logType = LogType.WARN,
                    shouldLog = true
                )
                Level.ERROR -> com.sarim.utils.log.log(
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
