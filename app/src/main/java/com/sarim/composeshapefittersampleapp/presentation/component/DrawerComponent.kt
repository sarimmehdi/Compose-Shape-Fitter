package com.sarim.composeshapefittersampleapp.presentation.component

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenEvents
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

const val DRAWER_COMPONENT_CLOSE_DRAWER_ICON_BUTTON_TEST_TAG = "DRAWER_COMPONENT_CLOSE_DRAWER_ICON_BUTTON_TEST_TAG"
const val DRAWER_COMPONENT_LAZY_COLUMN_TEST_TAG = "DRAWER_COMPONENT_LAZY_COLUMN_TEST_TAG"
const val DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_SELECTED_FOR_ = "DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_SELECTED_FOR_"
const val DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_ = "DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_"

@Composable
fun DrawerComponent(
    modifier: Modifier = Modifier,
    data: DrawerComponentData = DrawerComponentData(),
    onEvent: (DrawingScreenToViewModelEvents) -> Unit = {},
    onDrawingScreenEvent: (DrawingScreenEvents) -> Unit = {},
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    onDrawingScreenEvent(DrawingScreenEvents.CloseDrawer)
                },
                modifier = Modifier.testTag(DRAWER_COMPONENT_CLOSE_DRAWER_ICON_BUTTON_TEST_TAG)
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
            modifier = Modifier.testTag(DRAWER_COMPONENT_LAZY_COLUMN_TEST_TAG)
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
                        onDrawingScreenEvent(DrawingScreenEvents.CloseDrawer)
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag(
                            if (shape == data.selectedShape) {
                                DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_SELECTED_FOR_ + stringResource(shape.shapeStringId)
                            } else {
                                DRAWER_COMPONENT_SELECTED_NAVIGATION_DRAWER_ITEM_TEST_TAG_NOT_SELECTED_FOR_ + stringResource(shape.shapeStringId)
                            }
                        ),
                )
            }
        }
    }
}

data class DrawerComponentData(
    val allShapes: ImmutableList<Shape> = persistentListOf(),
    val selectedShape: Shape = Shape.Circle,
)
