package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.presentation.component.CanvasComponent
import com.sarim.composeshapefittersampleapp.presentation.component.CanvasComponentData
import com.sarim.composeshapefittersampleapp.utils.UiText
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

const val DEFAULT_STROKE_WIDTH = 5f

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun DrawingScreen(
    state: DrawingScreenState,
    onEvent: (DrawingScreenToViewModelEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = modifier) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.close() } }) {
                            Icon(Icons.Filled.Close, contentDescription = UiText.StringResource(R.string.close_nav_menu).asString())
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            UiText.StringResource(R.string.select_shape).asString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color,
                    )
                    LazyColumn {
                        items(state.allShapes.size) { i ->
                            val shape = state.allShapes[i]
                            NavigationDrawerItem(
                                label = {
                                    Text(UiText.StringResource(shape.shapeStringId).asString())
                                },
                                selected = shape == state.selectedShape,
                                onClick = {
                                    onEvent(
                                        DrawingScreenToViewModelEvents.SetSelectedShape(
                                            selectedShape = shape,
                                        ),
                                    )
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(UiText.StringResource(R.string.app_name).asString()) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = UiText.StringResource(R.string.open_drawer).asString(),
                            )
                        }
                    },
                    actions = {
                        // Add the actions parameter here
                        IconButton(onClick = {
                            onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = UiText.StringResource(R.string.settings).asString(),
                            )
                        }
                        DropdownMenu(
                            expanded = state.showSettingsDropDown,
                            onDismissRequest = { onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown) },
                        ) {
                            DropdownMenuItem(
                                text = { Text(UiText.StringResource(R.string.finger_traced_lines).asString()) },
                                onClick = {
                                    onEvent(
                                        DrawingScreenToViewModelEvents.ToggleSettings(
                                            DrawingScreenToViewModelEvents.ToggleSettings.Type.SHOW_FINGER_TRACED_LINES,
                                        ),
                                    )
                                    onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown)
                                },
                                trailingIcon = {
                                    if (state.showFingerTracedLines) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = UiText.StringResource(R.string.settings).asString(),
                                        )
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(UiText.StringResource(R.string.approximated_shape).asString()) },
                                onClick = {
                                    onEvent(
                                        DrawingScreenToViewModelEvents.ToggleSettings(
                                            DrawingScreenToViewModelEvents.ToggleSettings.Type.SHOW_APPROXIMATED_SHAPE,
                                        ),
                                    )
                                    onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown)
                                },
                                trailingIcon = {
                                    if (state.showApproximatedShape) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = UiText.StringResource(R.string.settings).asString(),
                                        )
                                    }
                                },
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
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

@Serializable
object DrawingScreenNavigationDestination
