package com.sarim.example_app_presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.example_app_domain.model.Shape
import com.sarim.utils.generateDummyPoints
import com.sarim.utils.getDrawableShapeFromShape
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
    device = PIXEL_6_PRO,
)
internal fun DrawingScreenPreviewType4(
    @PreviewParameter(DrawingScreenDataProviderType4::class) data: DrawingScreenData,
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

class DrawingScreenDataProviderType4 : PreviewParameterProvider<DrawingScreenData> {
    override val values =
        Shape.entries.asSequence().map { selectedShape ->
            val drawableShape = getDrawableShapeFromShape(selectedShape)
            val points = generateDummyPoints(drawableShape)
            val lines =
                points.zipWithNext { currentPoint, nextPoint ->
                    Pair(currentPoint, nextPoint)
                }
            DrawingScreenData(
                state =
                    DrawingScreenState(
                        selectedShape = selectedShape,
                        approximatedShape = drawableShape.getApproximatedShape(points),
                        points = points.toImmutableList(),
                        lines = lines.toImmutableList(),
                    ),
            )
        }
}
