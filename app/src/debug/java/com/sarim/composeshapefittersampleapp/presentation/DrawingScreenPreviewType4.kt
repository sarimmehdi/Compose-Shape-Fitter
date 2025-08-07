package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.NEXUS_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.composeshapefittersampleapp.utils.generateDummyPoints
import com.sarim.composeshapefittersampleapp.utils.getDrawableShapeFromShape
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun DrawingScreenPreviewType4(
    @PreviewParameter(DrawingScreenDataProviderType4::class) data: DrawingScreenData
) {
    ComposeShapeFitterSampleAppTheme {
        DrawingScreen(
            data = data
        )
    }
}

class DrawingScreenDataProviderType4 : PreviewParameterProvider<DrawingScreenData> {
    override val values = Shape.entries.asSequence().map { selectedShape ->
        val drawableShape = getDrawableShapeFromShape(selectedShape)
        val points = generateDummyPoints(drawableShape)
        val lines = points.zipWithNext { currentPoint, nextPoint ->
            Pair(currentPoint, nextPoint)
        }
        DrawingScreenData(
            state = DrawingScreenState(
                selectedShape = selectedShape,
                approximatedShape = drawableShape.getApproximatedShape(points),
                points = points.toImmutableList(),
                lines = lines.toImmutableList()
            ),
        )
    }
}
