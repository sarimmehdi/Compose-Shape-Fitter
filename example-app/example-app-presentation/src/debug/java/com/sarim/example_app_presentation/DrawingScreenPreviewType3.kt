package com.sarim.example_app_presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map

@Composable
@Preview(
    apiLevel = 35,
    device = PIXEL_6_PRO
)
internal fun DrawingScreenPreviewType3(
    @PreviewParameter(DrawingScreenDataProviderType3::class) data: DrawingScreenData
) {
    DrawingScreen(
        data = data.copy(
            state = data.state.copy(
                inPreviewMode = true
            )
        )
    )
}

class DrawingScreenDataProviderType3 : PreviewParameterProvider<DrawingScreenData> {
    override val values = Exhaustive.boolean().flatMap { showFingerTracedLinesValue ->
        Exhaustive.boolean().map { showApproximatedShapeValue ->
            DrawingScreenData(
                state = DrawingScreenState(
                    showSettingsDropDown = true,
                    showFingerTracedLines = showFingerTracedLinesValue,
                    showApproximatedShape = showApproximatedShapeValue
                )
            )
        }
    }.values.asSequence()
}
