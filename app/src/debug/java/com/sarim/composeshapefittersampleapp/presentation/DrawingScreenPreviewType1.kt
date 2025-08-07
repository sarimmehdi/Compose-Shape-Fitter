package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.NEXUS_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun DrawingScreenPreviewType1(
    @PreviewParameter(DrawingScreenDataProviderType1::class) data: DrawingScreenData
) {
    ComposeShapeFitterSampleAppTheme {
        DrawingScreen(
            data = data
        )
    }
}

class DrawingScreenDataProviderType1 : PreviewParameterProvider<DrawingScreenData> {
    override val values = Shape.entries.asSequence().map { selectedShape ->
        DrawingScreenData(
            state = DrawingScreenState(
                selectedShape = selectedShape
            ),
            drawerState = DrawerState(DrawerValue.Open)
        )
    }
}
