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
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun DrawingScreenPreviewType2(
    @PreviewParameter(DrawingScreenDataProviderType2::class) data: DrawingScreenData
) {
    ComposeShapeFitterSampleAppTheme {
        DrawingScreen(
            data = data
        )
    }
}

class DrawingScreenDataProviderType2 : PreviewParameterProvider<DrawingScreenData> {
    override val values = Shape.entries.asSequence().flatMap { selectedShape ->
        Exhaustive.boolean().values.flatMap { showFingerTracedLines ->
            Exhaustive.boolean().values.map { showApproximatedShape ->
                DrawingScreenData(
                    state = DrawingScreenState(
                        selectedShape = selectedShape,
                        showSettingsDropDown = true,
                        showFingerTracedLines = showFingerTracedLines,
                        showApproximatedShape = showApproximatedShape,
                    ),
                    drawerState = DrawerState(DrawerValue.Open)
                )
            }
        }
    }
}
