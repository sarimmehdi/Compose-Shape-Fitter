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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarim.composeshapefittersampleapp.R
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenEvents
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import com.sarim.composeshapefittersampleapp.utils.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
                }
            ) {
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
            items(data.allShapes.size) { i ->
                val shape = data.allShapes[i]
                NavigationDrawerItem(
                    label = {
                        Text(UiText.StringResource(shape.shapeStringId).asString())
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
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        }
    }
}

data class DrawerComponentData(
    val allShapes: ImmutableList<Shape> = persistentListOf(),
    val selectedShape: Shape = Shape.Circle,
)
