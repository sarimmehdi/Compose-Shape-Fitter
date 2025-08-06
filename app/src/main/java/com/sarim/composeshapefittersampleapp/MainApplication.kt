package com.sarim.composeshapefittersampleapp

import android.app.Application
import com.sarim.composeshapefittersampleapp.di.ModuleType
import com.sarim.composeshapefittersampleapp.di.drawingScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication :
    Application(),
    KoinStartup {
    override fun onKoinStartup() =
        koinConfiguration {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            lazyModules(
                drawingScreenModule(
                    scopeQualifier = named(DrawingFeature::class.java.simpleName),
                    moduleType = ModuleType.ACTUAL,
                ),
            )
        }
}
