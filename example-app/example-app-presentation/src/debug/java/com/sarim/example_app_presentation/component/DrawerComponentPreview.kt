package com.sarim.example_app_presentation.component

import androidx.compose.foundation.background
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.example_app_domain.model.Shape
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
)
internal fun DrawerComponentPreview(
    @PreviewParameter(DrawerComponentDataParameterProvider::class) data: DrawerComponentData,
) {
    DrawerComponent(
        data = data,
        modifier = Modifier.background(Color.White),
    )
}

class DrawerComponentDataParameterProvider : PreviewParameterProvider<DrawerComponentData> {
    override val values =
        Shape.entries.asSequence().flatMap { selectedShape ->
            DrawerValue.entries.map { drawerValue ->
                DrawerComponentData(
                    allShapes = Shape.entries.toImmutableList(),
                    selectedShape = selectedShape,
                    currentDrawerState = DrawerState(drawerValue),
                )
            }
        }
}
