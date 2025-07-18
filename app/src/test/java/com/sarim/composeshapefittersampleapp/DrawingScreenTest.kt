package com.sarim.composeshapefittersampleapp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import com.google.common.truth.Truth.assertThat
import com.sarim.compose_shape_fitter.CircleShape
import com.sarim.compose_shape_fitter.DrawableShape
import com.sarim.compose_shape_fitter.DrawingScreen
import com.sarim.compose_shape_fitter.EllipseShape
import com.sarim.compose_shape_fitter.Event
import com.sarim.compose_shape_fitter.HexagonShape
import com.sarim.compose_shape_fitter.ObbShape
import com.sarim.compose_shape_fitter.PentagonShape
import com.sarim.compose_shape_fitter.RectangleShape
import com.sarim.compose_shape_fitter.SkewedEllipseShape
import com.sarim.compose_shape_fitter.SquareShape
import com.sarim.compose_shape_fitter.TriangleShape
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal data class TestData(
    val drawableShape: DrawableShape,
    val dragStart: Offset,
    val dragPositions: List<Offset>,
) {
    val testDescription =
        when (drawableShape) {
            is CircleShape ->
                "draw a circle with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is EllipseShape ->
                "draw an ellipse with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is HexagonShape ->
                "draw a hexagon with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is ObbShape ->
                "draw an oriented bounding box with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}" +
                    if (drawableShape.allSidesEqual) " and all sides are equal" else ""
            is PentagonShape ->
                "draw a pentagon with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is RectangleShape ->
                "draw a rectangle with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is SkewedEllipseShape ->
                "draw an oriented ellipse with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is SquareShape ->
                "draw a square with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
            is TriangleShape ->
                "draw a triangle with " +
                    "color ${drawableShape.color} and " +
                    "stroke width ${drawableShape.strokeWidth}"
        }
}

@RunWith(ParameterizedRobolectricTestRunner::class)
internal class DrawingScreenTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestData,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        var lastPoints = listOf<Offset>()
        var lastApproximateShape: Any? = null

        composeTestRule.setContent {
            DrawingScreen(
                drawableShape = testData.drawableShape,
                onEvent = { event ->
                    when (event) {
                        is Event.ApproximateShapeChangedEvent -> {
                            lastApproximateShape = event.approximateShape
                        }
                        is Event.PointsChangedEvent -> {
                            lastPoints = event.points
                        }
                    }
                },
            )
        }

        composeTestRule.onRoot().performTouchInput {
            down(testData.dragStart)
            for (position in testData.dragPositions) {
                moveTo(position)
            }
            up()
        }

        composeTestRule.runOnIdle {
            assertThat(lastPoints.size).isEqualTo(testData.dragPositions.size + 1)
            assertThat(lastApproximateShape).isEqualTo(testData.drawableShape.getApproximatedShape(lastPoints))
        }
    }

    companion object {
        private fun generateEllipsePoints(
            centerX: Float,
            centerY: Float,
            radiusX: Float,
            radiusY: Float,
            numPoints: Int = 36,
        ): List<Offset> {
            val points = mutableListOf<Offset>()
            for (i in 0..numPoints) {
                val angle = i * (2 * PI / numPoints)
                val x = centerX + radiusX * cos(angle).toFloat()
                val y = centerY + radiusY * sin(angle).toFloat()
                points.add(Offset(x, y))
            }
            return points
        }

        private fun generatePolygonPoints(sides: Int): List<Offset> {
            val points = mutableListOf<Offset>()
            val angleStep = 2 * PI / sides
            for (i in 0..sides) { // iterate one extra time to close the polygon
                val angle = i * angleStep
                val x = 200 * cos(angle).toFloat()
                val y = 200 * sin(angle).toFloat()
                points.add(Offset(x, y))
            }
            return points
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(
            name = "{0}",
        )
        @Suppress("unused", "LongMethod")
        fun getParameters(): Collection<Array<Any>> {
            val commonStartX = 100f
            val commonStartY = 100f
            val defaultSize = 50f

            val circlePath = generateEllipsePoints(
                commonStartX + defaultSize,
                commonStartY + defaultSize,
                defaultSize,
                defaultSize
            )
            val ellipsePath = generateEllipsePoints(
                commonStartX + defaultSize,
                commonStartY + defaultSize,
                defaultSize * 1.5f,
                defaultSize
            )
            val hexagonPath = generatePolygonPoints(6)
            val pentagonPath = generatePolygonPoints(5)

            val allTestData =
                listOf(
                    TestData(
                        drawableShape = CircleShape(Color.Blue, 5f),
                        dragStart = circlePath.first(),
                        dragPositions = circlePath.drop(2),
                    ),
                    TestData(
                        drawableShape = EllipseShape(Color.Black, 5f),
                        dragStart = ellipsePath.first(),
                        dragPositions = ellipsePath.drop(2),
                    ),
                    TestData(
                        drawableShape = HexagonShape(Color.Black, 5f),
                        dragStart = hexagonPath.first(),
                        dragPositions = hexagonPath.drop(1),
                    ),
                    TestData( // Oriented Bounding Box (approximated as a rotated rectangle)
                        drawableShape = ObbShape(Color.Green, 5f, true),
                        dragStart = Offset(commonStartX, commonStartY),
                        dragPositions =
                            listOf(
                                Offset(commonStartX + defaultSize, commonStartY + defaultSize / 2),
                                Offset(commonStartX, commonStartY + defaultSize * 1.5f),
                                Offset(commonStartX - defaultSize, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY),
                            ),
                    ),
                    TestData(
                        drawableShape = ObbShape(Color.Cyan, 5f, false),
                        dragStart = Offset(commonStartX, commonStartY),
                        dragPositions =
                            listOf(
                                Offset(commonStartX + defaultSize * 1.5f, commonStartY + defaultSize / 3),
                                Offset(commonStartX + defaultSize * 0.5f, commonStartY + defaultSize * 1.8f),
                                Offset(commonStartX - defaultSize, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY),
                            ),
                    ),
                    TestData(
                        drawableShape = PentagonShape(Color.Magenta, 5f),
                        dragStart = pentagonPath.first(),
                        dragPositions = pentagonPath.drop(1),
                    ),
                    TestData(
                        drawableShape = RectangleShape(Color.Yellow, 5f),
                        dragStart = Offset(commonStartX, commonStartY),
                        dragPositions =
                            listOf(
                                Offset(commonStartX + defaultSize * 2, commonStartY),
                                Offset(commonStartX + defaultSize * 2, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY),
                            ),
                    ),
                    TestData(
                        drawableShape = SkewedEllipseShape(Color.DarkGray, 5f),
                        dragStart = Offset(commonStartX + 20f, commonStartY - 10f),
                        dragPositions =
                            generateEllipsePoints(
                                commonStartX + defaultSize,
                                commonStartY + defaultSize,
                                defaultSize,
                                defaultSize * 0.7f,
                                20,
                            ).map { it.copy(x = it.x + (it.y - commonStartY - defaultSize) * 0.3f) }
                                .drop(1),
                    ),
                    TestData(
                        drawableShape = SquareShape(Color.LightGray, 5f),
                        dragStart = Offset(commonStartX, commonStartY),
                        dragPositions =
                            listOf(
                                Offset(commonStartX + defaultSize, commonStartY),
                                Offset(commonStartX + defaultSize, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY),
                            ),
                    ),
                    TestData(
                        drawableShape = TriangleShape(Color.Red, 5f),
                        dragStart = Offset(commonStartX, commonStartY + defaultSize),
                        dragPositions =
                            listOf(
                                Offset(commonStartX + defaultSize / 2, commonStartY),
                                Offset(commonStartX + defaultSize, commonStartY + defaultSize),
                                Offset(commonStartX, commonStartY + defaultSize),
                            ),
                    ),
                )

            return allTestData.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}
