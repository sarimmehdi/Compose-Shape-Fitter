package com.sarim.composeshapefittersampleapp.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sarim.composeshapefittersampleapp.data.dto.settings.SettingsDtoSerializer
import com.sarim.composeshapefittersampleapp.data.dto.shape.ShapeDtoSerializer
import com.sarim.composeshapefittersampleapp.data.repository.SettingsRepositoryImpl
import com.sarim.composeshapefittersampleapp.data.repository.SettingsRepositoryTestImpl
import com.sarim.composeshapefittersampleapp.data.repository.ShapesRepositoryImpl
import com.sarim.composeshapefittersampleapp.data.repository.ShapesRepositoryTestImpl
import com.sarim.composeshapefittersampleapp.domain.repository.SettingsRepository
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.domain.usecase.GetAllShapesUseCase
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

enum class ModuleType {
    ACTUAL, TEST
}

fun drawingScreenModule(
    shapeDtoDataStoreName: String,
    settingsDtoDataStoreName: String,
    scope: StringQualifier,
    moduleType: ModuleType
) = lazyModule {
    single<ShapesRepository> {
        when (moduleType) {
            ModuleType.ACTUAL -> ShapesRepositoryImpl(
                dataStore =
                    DataStoreFactory.create(
                        serializer = ShapeDtoSerializer,
                        produceFile = { androidContext().dataStoreFile(shapeDtoDataStoreName) },
                    ),
            )
            ModuleType.TEST -> ShapesRepositoryTestImpl()
        }
    }

    single<SettingsRepository> {
        when (moduleType) {
            ModuleType.ACTUAL -> SettingsRepositoryImpl(
                dataStore =
                    DataStoreFactory.create(
                        serializer = SettingsDtoSerializer,
                        produceFile = { androidContext().dataStoreFile(settingsDtoDataStoreName) },
                    ),
            )
            ModuleType.TEST -> SettingsRepositoryTestImpl()
        }
    }

    viewModel {
        DrawingScreenViewModel(
            dispatchers = DefaultDispatchers(),
            savedStateHandle = get(),
            drawingScreenUseCases =
                DrawingScreenUseCases(
                    getSettingsUseCase = GetSettingsUseCase(get()),
                    getSelectedShapeUseCase = GetSelectedShapeUseCase(get()),
                    getAllShapesUseCase = GetAllShapesUseCase(),
                    updateSelectedShapeUseCase = UpdateSelectedShapeUseCase(get()),
                    updateSettingsUseCase = UpdateSettingsUseCase(get()),
                ),
        )
    }
}

fun drawingScreenModule(scopeQualifier: StringQualifier, moduleType: ModuleType) =
    drawingScreenModule(
        shapeDtoDataStoreName = ShapeDtoSerializer.SHAPE_DTO_DATA_STORE_NAME,
        settingsDtoDataStoreName = SettingsDtoSerializer.SETTINGS_DTO_DATA_STORE_NAME,
        scope = scopeQualifier,
        moduleType = moduleType
    )
