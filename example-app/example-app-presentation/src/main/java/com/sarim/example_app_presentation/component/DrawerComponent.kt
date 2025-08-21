package com.sarim.example_app_presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarim.compose_shape_fitter.BuildConfig
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.DrawingScreenToViewModelEvents
import com.sarim.example_app_presentation.R
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.CLOSE_DRAWER_ICON_BUTTON
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.DRAWER_COMPONENT
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.LAZY_COLUMN
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.utils.log.LogType
import com.sarim.utils.log.log
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun DrawerComponent(
    modifier: Modifier = Modifier,
    data: DrawerComponentData = DrawerComponentData(),
    onEvent: (DrawingScreenToViewModelEvents) -> Unit = {},
) {
    log(
        tag = "DrawerComponent",
        messageBuilder = {
            "data = $data"
        },
        logType = LogType.DEBUG,
        shouldLog = BuildConfig.DEBUG,
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier =
            modifier
                .padding(16.dp)
                .semantics { testTagsAsResourceId = true }
                .testTag(DRAWER_COMPONENT),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        data.currentDrawerState.close()
                    }
                },
                modifier =
                    Modifier
                        .semantics { testTagsAsResourceId = true }
                        .testTag(CLOSE_DRAWER_ICON_BUTTON),
            ) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close_nav_menu))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.select_shape),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color,
        )
        LazyColumn(
            modifier =
                Modifier
                    .semantics { testTagsAsResourceId = true }
                    .testTag(LAZY_COLUMN),
        ) {
            items(data.allShapes.size) { i ->
                val shape = data.allShapes[i]
                NavigationDrawerItem(
                    label = {
                        Text(stringResource(shape.shapeStringId))
                    },
                    selected = shape == data.selectedShape,
                    onClick = {
                        onEvent(
                            DrawingScreenToViewModelEvents.SetSelectedShape(
                                selectedShape = shape,
                            ),
                        )
                        scope.launch {
                            data.currentDrawerState.close()
                        }
                    },
                    modifier =
                        Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .semantics { testTagsAsResourceId = true }
                            .testTag(
                                if (shape == data.selectedShape) {
                                    SELECTED_NAVIGATION_DRAWER_ITEM +
                                        stringResource(
                                            shape.shapeStringId,
                                        )
                                } else {
                                    UNSELECTED_NAVIGATION_DRAWER_ITEM +
                                        stringResource(
                                            shape.shapeStringId,
                                        )
                                },
                            ),
                )
            }
        }
    }
}

data class DrawerComponentData(
    val allShapes: ImmutableList<Shape> = persistentListOf(),
    val selectedShape: Shape = Shape.Circle,
    val currentDrawerState: DrawerState = DrawerState(DrawerValue.Closed),
) {
    override fun toString(): String {
        val indent = "  "

        val allShapesString =
            if (allShapes.isEmpty()) {
                "allShapes: []"
            } else {
                "allShapes: [\n" +
                    allShapes.joinToString(separator = ",\n") { shape ->
                        "${indent}${indent}$shape"
                    } + "\n$indent]"
            }

        return """
            DrawerComponentData(
${indent}$allShapesString,
${indent}selectedShape: $selectedShape,
${indent}currentDrawerState: $currentDrawerState
)
            """.trimIndent()
    }
}

object DrawerComponentTestTags {
    private const val P = "DRAWER_COMPONENT_"
    private const val S = "_TEST_TAG"

    const val DRAWER_COMPONENT = "${P}$S"
    const val CLOSE_DRAWER_ICON_BUTTON = "${P}CLOSE_DRAWER_ICON_BUTTON$S"
    const val LAZY_COLUMN = "${P}LAZY_COLUMN$S"

    const val SELECTED_NAVIGATION_DRAWER_ITEM = "${P}SELECTED_NAVIGATION_DRAWER_ITEM${S}_FOR_"
    const val UNSELECTED_NAVIGATION_DRAWER_ITEM = "${P}UNSELECTED_NAVIGATION_DRAWER_ITEM${S}_FOR_"
}
