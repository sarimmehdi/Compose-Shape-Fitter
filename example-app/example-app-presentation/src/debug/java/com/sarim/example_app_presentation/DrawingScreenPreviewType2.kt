package com.sarim.example_app_presentation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.example_app_domain.model.Shape
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean

@Composable
@Preview(
    apiLevel = 35,
    device = PIXEL_6_PRO,
)
internal fun DrawingScreenPreviewType2(
    @PreviewParameter(DrawingScreenDataProviderType2::class) data: DrawingScreenData,
) {
    DrawingScreen(
        data =
            data.copy(
                state =
                    data.state.copy(
                        inPreviewMode = true,
                    ),
            ),
    )
}

class DrawingScreenDataProviderType2 : PreviewParameterProvider<DrawingScreenData> {
    override val values =
        Shape.entries.asSequence().flatMap { selectedShape ->
            Exhaustive.boolean().values.flatMap { showFingerTracedLines ->
                Exhaustive.boolean().values.map { showApproximatedShape ->
                    DrawingScreenData(
                        state =
                            DrawingScreenState(
                                selectedShape = selectedShape,
                                showSettingsDropDown = true,
                                showFingerTracedLines = showFingerTracedLines,
                                showApproximatedShape = showApproximatedShape,
                            ),
                        drawerState = DrawerState(DrawerValue.Open),
                    )
                }
            }
        }
}
