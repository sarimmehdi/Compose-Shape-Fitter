package com.sarim.example_app_di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sarim.example_app_data.dto.settings.SettingsDto
import com.sarim.example_app_data.dto.settings.SettingsDtoSerializer
import com.sarim.example_app_data.dto.shape.ShapeDto
import com.sarim.example_app_data.dto.shape.ShapeDtoSerializer
import com.sarim.example_app_data.repository.SettingsRepositoryImpl
import com.sarim.example_app_data.repository.ShapesRepositoryImpl
import com.sarim.example_app_domain.repository.SettingsRepository
import com.sarim.example_app_domain.repository.ShapesRepository
import com.sarim.example_app_domain.usecase.GetSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.GetSettingsUseCase
import com.sarim.example_app_domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.example_app_domain.usecase.UpdateSettingsUseCase
import com.sarim.example_app_presentation.BuildConfig
import com.sarim.example_app_presentation.DrawingFeature
import com.sarim.example_app_presentation.DrawingScreenUseCases
import com.sarim.example_app_presentation.DrawingScreenViewModel
import com.sarim.utils.test.DefaultDispatchers
import com.sarim.utils.test.ErrorDataStore
import com.sarim.utils.log.LogType
import com.sarim.utils.log.log
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.lazyModule

private const val SHAPE_DATASTORE = "shapeDataStore"
private const val SETTINGS_DATASTORE = "settingsDataStore"

internal fun dataStoreModule(
    moduleType: ModuleType,
) = lazyModule {
    val shapeDtoDataStoreName = moduleType.getShapeDtoDataStoreName()
    val settingsDtoDataStoreName = moduleType.getSettingsDtoDataStoreName()
    log(
        tag = "DrawingScreenModule",
        messageBuilder = {
            "called dataStoreModule " +
                "with shapeDtoDataStoreName = $shapeDtoDataStoreName, " +
                "settingsDtoDataStoreName = $settingsDtoDataStoreName, "
        },
        logType = LogType.DEBUG,
        shouldLog = BuildConfig.DEBUG,
    )

    single<DataStore<ShapeDto>>(named(SHAPE_DATASTORE)) {
        if (moduleType == ModuleType.TEST_ERROR) {
            ErrorDataStore(shapeDtoDataStoreName)
        } else {
            DataStoreFactory.create(
                serializer = ShapeDtoSerializer.create(shapeDtoDataStoreName),
                produceFile = { androidContext().dataStoreFile(shapeDtoDataStoreName) },
            )
        }
    }

    single<DataStore<SettingsDto>>(named(SETTINGS_DATASTORE)) {
        if (moduleType == ModuleType.TEST_ERROR) {
            ErrorDataStore(settingsDtoDataStoreName)
        } else {
            DataStoreFactory.create(
                serializer = SettingsDtoSerializer.create(settingsDtoDataStoreName),
                produceFile = { androidContext().dataStoreFile(settingsDtoDataStoreName) },
            )
        }
    }
}

internal fun drawingScreenModule(
    shapeDtoDataStoreName: String,
    settingsDtoDataStoreName: String,
    scope: StringQualifier
) =
    lazyModule {
        log(
            tag = "DrawingScreenModule",
            messageBuilder = {
                "called drawingScreenModule and scope = $scope"
            },
            logType = LogType.DEBUG,
            shouldLog = BuildConfig.DEBUG,
        )
        scope(scope) {
            scoped<ShapesRepository> {
                ShapesRepositoryImpl(
                    dataStore = get(named(SHAPE_DATASTORE)),
                    dataStoreName = shapeDtoDataStoreName
                )
            }

            scoped<SettingsRepository> {
                SettingsRepositoryImpl(
                    dataStore = get(named(SETTINGS_DATASTORE)),
                    dataStoreName = settingsDtoDataStoreName
                )
            }

            viewModel {
                DrawingScreenViewModel(
                    dispatchers = DefaultDispatchers(),
                    savedStateHandle = get(),
                    drawingScreenUseCases =
                        DrawingScreenUseCases(
                            getSettingsUseCase = GetSettingsUseCase(get()),
                            getSelectedShapeUseCase = GetSelectedShapeUseCase(get()),
                            updateSelectedShapeUseCase = UpdateSelectedShapeUseCase(get()),
                            updateSettingsUseCase = UpdateSettingsUseCase(get()),
                        ),
                )
            }
        }
    }

enum class ModuleType {
    ACTUAL,
    TEST,
    TEST_ERROR;
}

internal fun ModuleType.getShapeDtoDataStoreName() = when (this) {
    ModuleType.ACTUAL -> ShapeDtoSerializer.Companion.DataStoreType.ACTUAL.dataStoreName
    ModuleType.TEST -> ShapeDtoSerializer.Companion.DataStoreType.TEST.dataStoreName
    ModuleType.TEST_ERROR -> ShapeDtoSerializer.Companion.DataStoreType.TEST_ERROR.dataStoreName
}

internal fun ModuleType.getSettingsDtoDataStoreName() = when (this) {
    ModuleType.ACTUAL -> SettingsDtoSerializer.Companion.DataStoreType.ACTUAL.dataStoreName
    ModuleType.TEST -> SettingsDtoSerializer.Companion.DataStoreType.TEST.dataStoreName
    ModuleType.TEST_ERROR -> SettingsDtoSerializer.Companion.DataStoreType.TEST_ERROR.dataStoreName
}

fun drawingFeatureModules(moduleType: ModuleType) =
    listOf(
        dataStoreModule(
            moduleType = moduleType
        ),
        drawingScreenModule(
            shapeDtoDataStoreName = moduleType.getShapeDtoDataStoreName(),
            settingsDtoDataStoreName = moduleType.getSettingsDtoDataStoreName(),
            scope = named(DrawingFeature::class.java.name),
        ),
    )
