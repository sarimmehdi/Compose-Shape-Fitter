package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.NEXUS_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.compose_shape_fitter.shape.CircleShape
import com.sarim.compose_shape_fitter.shape.EllipseShape
import com.sarim.compose_shape_fitter.shape.HexagonShape
import com.sarim.compose_shape_fitter.shape.ObbShape
import com.sarim.compose_shape_fitter.shape.PentagonShape
import com.sarim.compose_shape_fitter.shape.RectangleShape
import com.sarim.compose_shape_fitter.shape.SkewedEllipseShape
import com.sarim.compose_shape_fitter.shape.SquareShape
import com.sarim.compose_shape_fitter.shape.TriangleShape
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.composeshapefittersampleapp.utils.generateDummyPoints
import com.sarim.composeshapefittersampleapp.utils.generateEllipsePoints
import com.sarim.composeshapefittersampleapp.utils.generatePolygonPoints
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import kotlinx.collections.immutable.toImmutableList

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun CanvasComponentPreview(
    @PreviewParameter(CanvasComponentDataParameterProvider::class) data: CanvasComponentData
) {
    ComposeShapeFitterSampleAppTheme {
        CanvasComponent(
            data = data,
            modifier = Modifier.background(Color.White)
        )
    }
}

class CanvasComponentDataParameterProvider : PreviewParameterProvider<CanvasComponentData> {
    override val values = listOf(
        CircleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        EllipseShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        HexagonShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        ObbShape(Color.Blue, DEFAULT_STROKE_WIDTH, allSidesEqual = true, inPreviewMode = true),
        ObbShape(Color.Blue, DEFAULT_STROKE_WIDTH, allSidesEqual = false, inPreviewMode = true),
        PentagonShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        RectangleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        SkewedEllipseShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        SquareShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
        TriangleShape(Color.Blue, DEFAULT_STROKE_WIDTH, inPreviewMode = true),
    ).asSequence().map { drawableShape ->
        Exhaustive.boolean().flatMap { isDragging ->
            Exhaustive.boolean().flatMap { showFingerTracedLines ->
                Exhaustive.boolean().map { showApproximatedShape ->
                    val points = generateDummyPoints(drawableShape)
                    val lines = points.zipWithNext { currentPoint, nextPoint ->
                        Pair(currentPoint, nextPoint)
                    }
                    CanvasComponentData(
                        drawableShape = drawableShape,
                        isDragging = isDragging,
                        points = points.toImmutableList(),
                        lines = lines.toImmutableList(),
                        showFingerTracedLines = showFingerTracedLines,
                        showApproximatedShape = showApproximatedShape
                    )
                }
            }
        }
    }.flatMap { exhaustiveData ->
        exhaustiveData.values
    }
}
