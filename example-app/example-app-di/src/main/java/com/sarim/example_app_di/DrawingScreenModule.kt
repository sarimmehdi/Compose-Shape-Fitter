package com.sarim.example_app_di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sarim.example_app_data.dto.settings.SettingsDtoSerializer
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
import org.koin.dsl.lazyModule

fun drawingScreenModule(
    shapeDtoDataStoreName: String,
    settingsDtoDataStoreName: String,
    scope: StringQualifier,
) = lazyModule {
    log(
        tag = "DrawingScreenModule",
        messageBuilder = {
            "called drawingScreenModule " +
                    "with shapeDtoDataStoreName = $shapeDtoDataStoreName, " +
                    "settingsDtoDataStoreName = $settingsDtoDataStoreName, " +
                    "and scope = $scope"
        },
        logType = LogType.DEBUG,
        shouldLog = BuildConfig.DEBUG
    )
    scope(scope) {
        scoped<ShapesRepository> {
            ShapesRepositoryImpl(
                dataStore =
                    DataStoreFactory.create(
                        serializer = ShapeDtoSerializer,
                        produceFile = { androidContext().dataStoreFile(shapeDtoDataStoreName) },
                    ),
            )
        }

        scoped<SettingsRepository> {
            SettingsRepositoryImpl(
                dataStore =
                    DataStoreFactory.create(
                        serializer = SettingsDtoSerializer,
                        produceFile = { androidContext().dataStoreFile(settingsDtoDataStoreName) },
                    ),
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

fun drawingScreenModule(
    scopeQualifier: StringQualifier,
    moduleType: ModuleType,
) = drawingScreenModule(
    shapeDtoDataStoreName =
        when (moduleType) {
            ModuleType.ACTUAL -> ShapeDtoSerializer.SHAPE_DTO_DATA_STORE_NAME
            ModuleType.TEST -> ShapeDtoSerializer.SHAPE_DTO_TEST_DATA_STORE_NAME
        },
    settingsDtoDataStoreName =
        when (moduleType) {
            ModuleType.ACTUAL -> SettingsDtoSerializer.SETTINGS_DTO_DATA_STORE_NAME
            ModuleType.TEST -> SettingsDtoSerializer.SETTINGS_DTO_TEST_DATA_STORE_NAME
        },
    scope = scopeQualifier,
)

enum class ModuleType {
    ACTUAL,
    TEST,
}
