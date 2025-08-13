package com.sarim.test_app

import android.app.Application
import com.sarim.example_app_di.ModuleType
import com.sarim.example_app_di.drawingFeatureModules
import com.sarim.utils.CustomKoinLogger
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
internal class TestApplication :
    Application(),
    KoinStartup {
    override fun onKoinStartup() =
        koinConfiguration {
            logger(
                CustomKoinLogger(
                    tag = TestApplication::class.java.simpleName,
                    level = Level.DEBUG
                )
            )
            androidContext(this@TestApplication)
            lazyModules(drawingFeatureModules(ModuleType.TEST))
        }
}
