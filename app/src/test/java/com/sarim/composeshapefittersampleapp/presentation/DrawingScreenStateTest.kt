package com.sarim.composeshapefittersampleapp.presentation

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.sarim.compose_shape_fitter.shape.CircleShape
import com.sarim.compose_shape_fitter.shape.DrawableShape
import com.sarim.compose_shape_fitter.shape.EllipseShape
import com.sarim.compose_shape_fitter.shape.HexagonShape
import com.sarim.compose_shape_fitter.shape.ObbShape
import com.sarim.compose_shape_fitter.shape.PentagonShape
import com.sarim.compose_shape_fitter.shape.RectangleShape
import com.sarim.compose_shape_fitter.shape.SkewedEllipseShape
import com.sarim.compose_shape_fitter.shape.SquareShape
import com.sarim.compose_shape_fitter.shape.TriangleShape
import com.sarim.composeshapefittersampleapp.domain.model.Shape
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenStateTest(
    val inputShape: Shape,
    val inputColor: Color,
    val inputStrokeWidth: Float,
    val expectedOutput: DrawableShape,
) {
    val testDescription =
        "when input shape is $inputShape, " +
                "input color is $inputColor, " +
                "and input stroke width is $inputStrokeWidth" +
                "the expected output should be $expectedOutput"
}

@RunWith(Parameterized::class)
class DrawingScreenStateTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenStateTest,
) {

    @Test
    fun test() {
        assertThat(
            DrawingScreenState(
                selectedShape = testData.inputShape
            ).getDrawableShape(testData.inputColor, testData.inputStrokeWidth)
        ).isEqualTo(testData.expectedOutput)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused", "LongMethod")
        fun getParameters(): Collection<Array<Any>> {
            val color = mockk<Color>(relaxed = true)
            val strokeWidth = mockk<Float>(relaxed = true)
            return Shape.entries.map {
                when (it) {
                    Shape.Circle -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = CircleShape(color, strokeWidth)
                    )
                    Shape.Ellipse -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = EllipseShape(color, strokeWidth)
                    )
                    Shape.Hexagon -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = HexagonShape(color, strokeWidth)
                    )
                    Shape.OrientedRectangle -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = ObbShape(color, strokeWidth, false)
                    )
                    Shape.OrientedSquare -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = ObbShape(color, strokeWidth, true)
                    )
                    Shape.Pentagon -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = PentagonShape(color, strokeWidth)
                    )
                    Shape.Rectangle -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = RectangleShape(color, strokeWidth)
                    )
                    Shape.OrientedEllipse -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = SkewedEllipseShape(color, strokeWidth)
                    )
                    Shape.Square -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = SquareShape(color, strokeWidth)
                    )
                    Shape.Triangle -> TestDataDrawingScreenStateTest(
                        inputShape = it,
                        inputColor = color,
                        inputStrokeWidth = strokeWidth,
                        expectedOutput = TriangleShape(color, strokeWidth)
                    )
                }
            }.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}