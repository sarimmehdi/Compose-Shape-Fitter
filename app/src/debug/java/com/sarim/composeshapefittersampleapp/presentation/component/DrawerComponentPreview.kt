package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.NEXUS_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun DrawerComponentPreview(
    @PreviewParameter(DrawerComponentDataProvider::class) data: DrawerComponentData
) {
    ComposeShapeFitterSampleAppTheme {
        DrawerComponent(
            data = data,
            modifier = Modifier.background(Color.White)
        )
    }
}

class DrawerComponentDataProvider : PreviewParameterProvider<DrawerComponentData> {
    override val values = Shape.entries.asSequence().flatMap { selectedShape ->
        DrawerValue.entries.map { drawerValue ->
            DrawerComponentData(
                allShapes = Shape.entries.toImmutableList(),
                selectedShape = selectedShape,
                currentDrawerState = DrawerState(drawerValue)
            )
        }
    }
}
