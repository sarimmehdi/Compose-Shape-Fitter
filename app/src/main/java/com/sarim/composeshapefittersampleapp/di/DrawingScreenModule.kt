package com.sarim.composeshapefittersampleapp.di

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.sarim.composeshapefittersampleapp.data.dto.ShapeDtoSerializer
import com.sarim.composeshapefittersampleapp.data.repository.ShapesRepositoryImpl
import com.sarim.composeshapefittersampleapp.domain.repository.ShapesRepository
import com.sarim.composeshapefittersampleapp.domain.usecase.GetAllShapesUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.GetSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.domain.usecase.UpdateSelectedShapeUseCase
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenUseCases
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.lazyModule

fun drawingScreenModule(dataStoreName: String, scope: StringQualifier) = lazyModule {
    single<ShapesRepository> {
        ShapesRepositoryImpl(
            dataStore = DataStoreFactory.create(
                serializer = ShapeDtoSerializer,
                produceFile = { androidContext().dataStoreFile(dataStoreName) },
            ),
        )
    }

    viewModel {
        DrawingScreenViewModel(
            savedStateHandle = get(),
            drawingScreenUseCases =
                DrawingScreenUseCases(
                    getSelectedShapeUseCase = GetSelectedShapeUseCase(get()),
                    getAllShapesUseCase = GetAllShapesUseCase(get()),
                    updateSelectedShapeUseCase = UpdateSelectedShapeUseCase(get())
                ),
        )
    }
}

fun drawingScreenActualModule(scopeQualifier: StringQualifier) = drawingScreenModule(
    dataStoreName = ShapeDtoSerializer.SELECTED_SHAPE_DTO_DATA_STORE_NAME,
    scope = scopeQualifier
)

