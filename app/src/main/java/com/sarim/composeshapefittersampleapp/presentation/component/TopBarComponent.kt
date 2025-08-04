package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import kotlinx.coroutines.launch

const val TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG = "TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG"
const val TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG = "TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    data: TopBarComponentData = TopBarComponentData(),
    onEvent: (DrawingScreenToViewModelEvents) -> Unit = {},
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        data.currentDrawerState.open()
                    }
                },
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(TOP_BAR_COMPONENT_OPEN_DRAWER_ICON_BUTTON_TEST_TAG)
            ) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.open_drawer),
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown)
                },
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(TOP_BAR_COMPONENT_SETTINGS_ICON_BUTTON_TEST_TAG)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                )
            }
            DropdownMenu(
                expanded = data.showSettingsDropDown,
                onDismissRequest = { onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown) },
                modifier = Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_TEST_TAG)
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.finger_traced_lines)) },
                    onClick = {
                        onEvent(
                            DrawingScreenToViewModelEvents.ToggleSettings(
                                showFingerTracedLines = !data.showFingerTracedLines,
                                showApproximatedShape = data.showApproximatedShape
                            ),
                        )
                    },
                    trailingIcon = {
                        if (data.showFingerTracedLines) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.settings),
                                modifier = Modifier
                                    .semantics { testTagsAsResourceId = true }
                                    .testTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TRAILING_ICON_TEST_TAG)
                            )
                        }
                    },
                    modifier = Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_FINGER_TRACED_LINES_TEST_TAG)
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.approximated_shape)) },
                    onClick = {
                        onEvent(
                            DrawingScreenToViewModelEvents.ToggleSettings(
                                showFingerTracedLines = data.showFingerTracedLines,
                                showApproximatedShape = !data.showApproximatedShape
                            ),
                        )
                    },
                    trailingIcon = {
                        if (data.showApproximatedShape) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.settings),
                                modifier = Modifier
                                    .semantics { testTagsAsResourceId = true }
                                    .testTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TRAILING_ICON_TEST_TAG)
                            )
                        }
                    },
                    modifier = Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(TOP_BAR_COMPONENT_SETTINGS_DROP_DOWN_MENU_ITEM_APPROXIMATED_SHAPE_TEST_TAG)
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        modifier = modifier
    )
}

data class TopBarComponentData(
    val showSettingsDropDown: Boolean = false,
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
    val currentDrawerState: DrawerState = DrawerState(DrawerValue.Closed),
)
