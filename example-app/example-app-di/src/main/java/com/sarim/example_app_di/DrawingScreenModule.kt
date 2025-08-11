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
import com.sarim.example_app_presentation.DrawingScreenUseCases
import com.sarim.example_app_presentation.DrawingScreenViewModel
import com.sarim.utils.DefaultDispatchers
import com.sarim.utils.LogType
import com.sarim.utils.log
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.lazyModule

const val SHAPE_DATASTORE = "shapeDataStore"
const val SETTINGS_DATASTORE = "settingsDataStore"

fun dataStoreModule(
    shapeDtoDataStoreName: String,
    settingsDtoDataStoreName: String
) = lazyModule {
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
        DataStoreFactory.create(
            serializer = ShapeDtoSerializer,
            produceFile = { androidContext().dataStoreFile(shapeDtoDataStoreName) }
        )
    }

    single<DataStore<SettingsDto>>(named(SETTINGS_DATASTORE)) {
        DataStoreFactory.create(
            serializer = SettingsDtoSerializer,
            produceFile = { androidContext().dataStoreFile(settingsDtoDataStoreName) }
        )
    }
}

fun drawingScreenModule(
    scope: StringQualifier,
) = lazyModule {
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
            )
        }

        scoped<SettingsRepository> {
            SettingsRepositoryImpl(
                dataStore = get(named(SETTINGS_DATASTORE)),
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
