package com.sarim.example_app_presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.sarim.example_app_presentation.DrawerScreenTestTags.SNACKBAR
import com.sarim.example_app_presentation.component.CanvasComponent
import com.sarim.example_app_presentation.component.CanvasComponentData
import com.sarim.example_app_presentation.component.DrawerComponent
import com.sarim.example_app_presentation.component.DrawerComponentData
import com.sarim.example_app_presentation.component.TopBarComponent
import com.sarim.example_app_presentation.component.TopBarComponentData
import kotlinx.serialization.Serializable

const val DEFAULT_STROKE_WIDTH = 5f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    data: DrawingScreenData = DrawingScreenData(),
    onEvent: (DrawingScreenToViewModelEvents) -> Unit = {},
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = data.snackbarHostState,
                modifier =
                    Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(SNACKBAR),
            )
        },
        modifier = modifier,
    ) { padding ->
        ModalNavigationDrawer(
            drawerState = data.drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerComponent(
                        data =
                            DrawerComponentData(
                                allShapes = data.state.allShapes,
                                selectedShape = data.state.selectedShape,
                                currentDrawerState = data.drawerState,
                            ),
                        onEvent = onEvent,
                    )
                }
            },
            modifier = Modifier.padding(padding),
        ) {
            Scaffold(
                topBar = {
                    TopBarComponent(
                        data =
                            TopBarComponentData(
                                showSettingsDropDown = data.state.showSettingsDropDown,
                                showFingerTracedLines = data.state.showFingerTracedLines,
                                showApproximatedShape = data.state.showApproximatedShape,
                                currentDrawerState = data.drawerState,
                            ),
                        onEvent = onEvent,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                CanvasComponent(
                    data =
                        CanvasComponentData(
                            drawableShape =
                                data.state.getDrawableShape(
                                    Color.Blue,
                                    DEFAULT_STROKE_WIDTH,
                                ),
                            isDragging = data.state.isDragging,
                            points = data.state.points,
                            lines = data.state.lines,
                            showFingerTracedLines = data.state.showFingerTracedLines,
                            showApproximatedShape = data.state.showApproximatedShape,
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
}

data class DrawingScreenData(
    val state: DrawingScreenState = DrawingScreenState(),
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
)

object DrawerScreenTestTags {
    const val SNACKBAR = "SNACKBAR"
}

@Serializable
object DrawingScreenNavigationDestination

@Serializable
data object DrawingFeature
