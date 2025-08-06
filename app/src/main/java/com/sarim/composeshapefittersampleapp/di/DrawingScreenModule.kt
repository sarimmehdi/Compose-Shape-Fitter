package com.sarim.composeshapefittersampleapp.di

import android.util.Log
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sarim.composeshapefittersampleapp.data.dto.settings.SettingsDtoSerializer
import com.sarim.composeshapefittersampleapp.data.dto.shape.ShapeDtoSerializer
import com.sarim.composeshapefittersampleapp.data.repository.SettingsRepositoryImpl
import com.sarim.composeshapefittersampleapp.data.repository.ShapesRepositoryImpl
import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.domain.usecase.GetSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.GetSettingsUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSettingsUseCase
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenUseCases
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel
import com.sarim.composeshapefittersampleapp.utils.DefaultDispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.lazyModule

fun drawingScreenModule(
    shapeDtoDataStoreName: String,
    settingsDtoDataStoreName: String,
    scope: StringQualifier,
) = lazyModule {
    Log.d(
        "DrawingScreenModule",
        "called drawingScreenModule " +
            "with shapeDtoDataStoreName = $shapeDtoDataStoreName, " +
            "settingsDtoDataStoreName = $settingsDtoDataStoreName, " +
            "and scope = $scope",
    )
    single<ShapesRepository> {
        ShapesRepositoryImpl(
            dataStore =
                DataStoreFactory.create(
                    serializer = ShapeDtoSerializer,
                    produceFile = { androidContext().dataStoreFile(shapeDtoDataStoreName) },
                ),
        )
    }

    single<SettingsRepository> {
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
