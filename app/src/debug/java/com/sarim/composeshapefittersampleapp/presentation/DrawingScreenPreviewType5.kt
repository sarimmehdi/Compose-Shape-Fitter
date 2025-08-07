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
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun DrawingScreenPreviewType5(
    @PreviewParameter(DrawingScreenDataProviderType5::class) data: DrawingScreenData
) {
    ComposeShapeFitterSampleAppTheme {
        DrawingScreen(
            data = data
        )
    }
}

class DrawingScreenDataProviderType5 : PreviewParameterProvider<DrawingScreenData> {
    override val values = Shape.entries.asSequence().flatMap { selectedShape ->
        val drawableShape = getDrawableShapeFromShape(selectedShape)
        val points = generateDummyPoints(drawableShape)
        val lines = points.zipWithNext { currentPoint, nextPoint ->
            Pair(currentPoint, nextPoint)
        }
        Exhaustive.boolean().values.flatMap { showFingerTracedLines ->
            Exhaustive.boolean().values.map { showApproximatedShape ->
                DrawingScreenData(
                    state = DrawingScreenState(
                        selectedShape = selectedShape,
                        approximatedShape = drawableShape.getApproximatedShape(points),
                        points = points.toImmutableList(),
                        lines = lines.toImmutableList(),
                        showSettingsDropDown = true,
                        showFingerTracedLines = showFingerTracedLines,
                        showApproximatedShape = showFingerTracedLines
                    ),
                )
            }
        }
    }
}
