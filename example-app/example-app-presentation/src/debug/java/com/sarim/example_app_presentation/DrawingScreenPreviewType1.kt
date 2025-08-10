package com.sarim.example_app_presentation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.example_app_domain.model.Shape

@Composable
@Preview(
    apiLevel = 35,
    device = PIXEL_6_PRO
)
internal fun DrawingScreenPreviewType1(
    @PreviewParameter(DrawingScreenDataProviderType1::class) data: DrawingScreenData
) {
    DrawingScreen(
        data = data.copy(
            state = data.state.copy(
                inPreviewMode = true
            )
        )
    )
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
