package com.sarim.composeshapefittersampleapp

import android.app.Application
import com.sarim.example_app_data.dto.settings.SettingsDtoSerializer
import com.sarim.example_app_data.dto.shape.ShapeDtoSerializer
import com.sarim.example_app_di.dataStoreModule
import com.sarim.example_app_di.drawingScreenModule
import com.sarim.example_app_presentation.DrawingFeature
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class TestApplication :
    Application(),
    KoinStartup {
    override fun onKoinStartup() =
        koinConfiguration {
            androidLogger(Level.DEBUG)
            androidContext(this@TestApplication)
            lazyModules(
                dataStoreModule(
                    shapeDtoDataStoreName = ShapeDtoSerializer.SHAPE_DTO_TEST_DATA_STORE_NAME,
                    settingsDtoDataStoreName = SettingsDtoSerializer.SETTINGS_DTO_TEST_DATA_STORE_NAME,
                ),
                drawingScreenModule(
                    scope = named(DrawingFeature::class.java.name),
                ),
            )
        }
}
