package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sarim.composeshapefittersampleapp.presentation.component.CanvasComponent
import com.sarim.composeshapefittersampleapp.presentation.component.CanvasComponentData
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponent
import com.sarim.composeshapefittersampleapp.presentation.component.DrawerComponentData
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponent
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentData
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

const val DEFAULT_STROKE_WIDTH = 5f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    state: DrawingScreenState,
    onEvent: (DrawingScreenToViewModelEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onDrawingScreenEvent: (DrawingScreenEvents) -> Unit = {
        when (it) {
            DrawingScreenEvents.CloseDrawer -> scope.launch {
                drawerState.close()
            }
            DrawingScreenEvents.OpenDrawer -> scope.launch {
                drawerState.open()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = modifier) {
                DrawerComponent(
                    data = DrawerComponentData(
                        allShapes = state.allShapes,
                        selectedShape = state.selectedShape
                    ),
                    onEvent = onEvent,
                    onDrawingScreenEvent = onDrawingScreenEvent
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopBarComponent(
                    data = TopBarComponentData(
                        showSettingsDropDown = state.showSettingsDropDown,
                        showFingerTracedLines = state.showFingerTracedLines,
                        showApproximatedShape = state.showApproximatedShape
                    ),
                    onEvent = onEvent,
                    onDrawingScreenEvent = onDrawingScreenEvent
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            CanvasComponent(
                data =
                    CanvasComponentData(
                        drawableShape = state.getDrawableShape(Color.Blue, DEFAULT_STROKE_WIDTH),
                        isDragging = state.isDragging,
                        points = state.points,
                        lines = state.lines,
                        showFingerTracedLines = state.showFingerTracedLines,
                        showApproximatedShape = state.showApproximatedShape,
                    ),
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                onEvent = onEvent,
            )
        }
    }
}

sealed interface DrawingScreenEvents {
    data object CloseDrawer : DrawingScreenEvents
    data object OpenDrawer : DrawingScreenEvents
}

@Serializable
object DrawingScreenNavigationDestination
