package com.sarim.composeshapefittersampleapp

import android.app.Application
import com.sarim.example_app_di.ModuleType
import com.sarim.example_app_di.drawingFeatureModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication :
    Application(),
    KoinStartup {
    override fun onKoinStartup() =
        koinConfiguration {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            }
            androidContext(this@MainApplication)
            lazyModules(drawingFeatureModules(ModuleType.ACTUAL))
        }
}
