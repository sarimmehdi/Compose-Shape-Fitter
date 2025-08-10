package com.sarim.example_app_presentation.component

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
import com.sarim.compose_shape_fitter.BuildConfig
import com.sarim.example_app_presentation.DrawingScreenToViewModelEvents
import com.sarim.example_app_presentation.R
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES_TRAILING_ICON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.OPEN_DRAWER_ICON_BUTTON
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.SETTINGS_DROP_DOWN_MENU
import com.sarim.example_app_presentation.component.TopBarComponentTestTags.SETTINGS_ICON_BUTTON
import com.sarim.utils.LogType
import com.sarim.utils.log
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    data: TopBarComponentData = TopBarComponentData(),
    onEvent: (DrawingScreenToViewModelEvents) -> Unit = {},
) {
    log(
        tag = "TopBarComponent",
        messageBuilder = {
            "data = $data"
        },
        logType = LogType.DEBUG,
        shouldLog = BuildConfig.DEBUG
    )

    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text(stringResource(R.string.app_bar_title)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        data.currentDrawerState.open()
                    }
                },
                modifier =
                    Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(OPEN_DRAWER_ICON_BUTTON),
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
                modifier =
                    Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(SETTINGS_ICON_BUTTON),
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                )
            }
            DropdownMenu(
                expanded = data.showSettingsDropDown,
                onDismissRequest = { onEvent(DrawingScreenToViewModelEvents.ToggleSettingsDropDown) },
                modifier =
                    Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(SETTINGS_DROP_DOWN_MENU),
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.finger_traced_lines)) },
                    onClick = {
                        onEvent(
                            DrawingScreenToViewModelEvents.ToggleSettings(
                                showFingerTracedLines = !data.showFingerTracedLines,
                                showApproximatedShape = data.showApproximatedShape,
                            ),
                        )
                    },
                    trailingIcon = {
                        if (data.showFingerTracedLines) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.settings),
                                modifier =
                                    Modifier
                                        .semantics { testTagsAsResourceId = true }
                                        .testTag(FINGER_TRACED_LINES_TRAILING_ICON),
                            )
                        }
                    },
                    modifier =
                        Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag(FINGER_TRACED_LINES),
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.approximated_shape)) },
                    onClick = {
                        onEvent(
                            DrawingScreenToViewModelEvents.ToggleSettings(
                                showFingerTracedLines = data.showFingerTracedLines,
                                showApproximatedShape = !data.showApproximatedShape,
                            ),
                        )
                    },
                    trailingIcon = {
                        if (data.showApproximatedShape) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.settings),
                                modifier =
                                    Modifier
                                        .semantics { testTagsAsResourceId = true }
                                        .testTag(APPROXIMATED_SHAPE_TRAILING_ICON),
                            )
                        }
                    },
                    modifier =
                        Modifier
                            .semantics { testTagsAsResourceId = true }
                            .testTag(APPROXIMATED_SHAPE),
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        modifier = modifier,
    )
}

data class TopBarComponentData(
    val showSettingsDropDown: Boolean = false,
    val showFingerTracedLines: Boolean = true,
    val showApproximatedShape: Boolean = true,
    val currentDrawerState: DrawerState = DrawerState(DrawerValue.Closed),
) {
    override fun toString(): String {
        val indent = "  "
        return """
            TopBarComponentData(
${indent}showSettingsDropDown: $showSettingsDropDown,
${indent}showFingerTracedLines: $showFingerTracedLines,
${indent}showApproximatedShape: $showApproximatedShape,
${indent}currentDrawerState: $currentDrawerState
)
            """.trimIndent()
    }
}

object TopBarComponentTestTags {
    private const val P = "TOP_BAR_COMPONENT_"
    private const val S = "_TEST_TAG"

    const val OPEN_DRAWER_ICON_BUTTON = "${P}OPEN_DRAWER_ICON_BUTTON$S"
    const val SETTINGS_ICON_BUTTON = "${P}SETTINGS_ICON_BUTTON$S"
    const val SETTINGS_DROP_DOWN_MENU = "${P}SETTINGS_DROP_DOWN_MENU$S"

    private const val SDDMI = "SETTINGS_DROP_DOWN_MENU_ITEM_"

    const val FINGER_TRACED_LINES = "${P}${SDDMI}FINGER_TRACED_LINES$S"
    const val FINGER_TRACED_LINES_TRAILING_ICON = "${P}${SDDMI}FINGER_TRACED_LINES_TRAILING_ICON$S"
    const val APPROXIMATED_SHAPE = "${P}${SDDMI}APPROXIMATED_SHAPE$S"
    const val APPROXIMATED_SHAPE_TRAILING_ICON = "${P}${SDDMI}APPROXIMATED_SHAPE_TRAILING_ICON$S"
}
