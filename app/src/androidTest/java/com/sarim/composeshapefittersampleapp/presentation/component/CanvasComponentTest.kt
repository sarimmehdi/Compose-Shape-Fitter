package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
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
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenToViewModelEvents
import io.mockk.mockk
import io.mockk.verifyOrder
import junit.framework.TestCase.fail
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class TestDataDrawingScreenTest(
    val dragStart: Offset,
    val dragPositions: List<Offset>,
    val data: CanvasComponentData,
    val expectedEvents: List<DrawingScreenToViewModelEvents>,
) {
    val testDescription =
        "when you start dragging from $dragStart, " +
                "and go through points: $dragPositions " +
                "with data $data" +
                "the expected sequence of events should be $expectedEvents"
}

@RunWith(Parameterized::class)
class CanvasComponentTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenTest,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    val onEvent: (DrawingScreenToViewModelEvents) -> Unit = mockk(relaxed = true)

    @Test
    fun test() {
        composeTestRule.setContent {
            CanvasComponent(
                data = testData.data,
                onEvent = onEvent,
            )
        }

        composeTestRule.onRoot().performTouchInput {
            down(testData.dragStart)
            for (dragPosition in testData.dragPositions) {
                moveTo(dragPosition)
            }
            up()
        }

        composeTestRule.runOnIdle {
            verifyOrder {
                testData.expectedEvents.forEach {
                    when (it) {
                        is DrawingScreenToViewModelEvents.SetApproximateShape ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.SetApproximateShape>())
                        is DrawingScreenToViewModelEvents.SetDragging ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.SetDragging>())
                        is DrawingScreenToViewModelEvents.SetLines ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.SetLines>())
                        is DrawingScreenToViewModelEvents.SetPoints ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.SetPoints>())
                        is DrawingScreenToViewModelEvents.UpdateLines ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.UpdateLines>())
                        is DrawingScreenToViewModelEvents.UpdatePoints ->
                            onEvent(ofType<DrawingScreenToViewModelEvents.UpdatePoints>())
                        else -> {
                            fail("received unexpected event: $it")
                        }
                    }
                }
            }
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
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> {
            val commonStartX = 100f
            val commonStartY = 100f
            val defaultSize = 50f

            val circlePath =
                generateEllipsePoints(
                    commonStartX + defaultSize,
                    commonStartY + defaultSize,
                    defaultSize,
                    defaultSize,
                )
            val ellipsePath =
                generateEllipsePoints(
                    commonStartX + defaultSize,
                    commonStartY + defaultSize,
                    defaultSize * 1.5f,
                    defaultSize,
                )
            val hexagonPath = generatePolygonPoints(6)
            val pentagonPath = generatePolygonPoints(5)

            data class DragPositionsAndResultingDrawableShape(
                val drawableShape: DrawableShape,
                val dragStart: Offset,
                val dragPositions: List<Offset>,
            )

            val allDragPositionsAndResultingDrawableShape =
                listOf(
                    DragPositionsAndResultingDrawableShape(
                        drawableShape = CircleShape(Color.Blue, 5f),
                        dragStart = circlePath.first(),
                        dragPositions = circlePath.drop(2),
                    ),
                    DragPositionsAndResultingDrawableShape(
                        drawableShape = EllipseShape(Color.Black, 5f),
                        dragStart = ellipsePath.first(),
                        dragPositions = ellipsePath.drop(2),
                    ),
                    DragPositionsAndResultingDrawableShape(
                        drawableShape = HexagonShape(Color.Black, 5f),
                        dragStart = hexagonPath.first(),
                        dragPositions = hexagonPath.drop(1),
                    ),
                    DragPositionsAndResultingDrawableShape(
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
                    DragPositionsAndResultingDrawableShape(
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
                    DragPositionsAndResultingDrawableShape(
                        drawableShape = PentagonShape(Color.Magenta, 5f),
                        dragStart = pentagonPath.first(),
                        dragPositions = pentagonPath.drop(1),
                    ),
                    DragPositionsAndResultingDrawableShape(
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
                    DragPositionsAndResultingDrawableShape(
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
                    DragPositionsAndResultingDrawableShape(
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
                    DragPositionsAndResultingDrawableShape(
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
            val allTestData =
                allDragPositionsAndResultingDrawableShape
                    .map {
                        listOf(
                            TestDataDrawingScreenTest(
                                dragStart = it.dragStart,
                                dragPositions = it.dragPositions,
                                data =
                                    CanvasComponentData(
                                        drawableShape = it.drawableShape,
                                        isDragging = true,
                                        points = persistentListOf(),
                                        lines = persistentListOf(),
                                        showFingerTracedLines = true,
                                        showApproximatedShape = true,
                                    ),
                                expectedEvents =
                                    listOf(
                                        DrawingScreenToViewModelEvents.SetDragging(true),
                                        DrawingScreenToViewModelEvents.SetLines(persistentListOf()),
                                        mockk<DrawingScreenToViewModelEvents.SetPoints>(),
                                    ) +
                                            it.dragPositions.drop(1).flatMapIndexed { index, currentDragPosition ->
                                                listOf(
                                                    mockk<DrawingScreenToViewModelEvents.UpdateLines>(),
                                                    mockk<DrawingScreenToViewModelEvents.UpdatePoints>(),
                                                )
                                            } +
                                            listOf(
                                                DrawingScreenToViewModelEvents.SetDragging(false),
                                            ),
                            ),
                            TestDataDrawingScreenTest(
                                dragStart = it.dragStart,
                                dragPositions = it.dragPositions,
                                data =
                                    CanvasComponentData(
                                        drawableShape = it.drawableShape,
                                        isDragging = true,
                                        points = persistentListOf(Offset.Zero),
                                        lines = persistentListOf(),
                                        showFingerTracedLines = true,
                                        showApproximatedShape = true,
                                    ),
                                expectedEvents =
                                    listOf(
                                        DrawingScreenToViewModelEvents.SetDragging(true),
                                        DrawingScreenToViewModelEvents.SetLines(persistentListOf()),
                                        mockk<DrawingScreenToViewModelEvents.SetPoints>(),
                                    ) +
                                            it.dragPositions.drop(1).flatMapIndexed { index, currentDragPosition ->
                                                listOf(
                                                    mockk<DrawingScreenToViewModelEvents.UpdateLines>(),
                                                    mockk<DrawingScreenToViewModelEvents.UpdatePoints>(),
                                                )
                                            } +
                                            listOf(
                                                DrawingScreenToViewModelEvents.SetDragging(false),
                                                DrawingScreenToViewModelEvents.SetApproximateShape(
                                                    when (it.drawableShape) {
                                                        is CircleShape -> mockk<CircleShape.Circle>(relaxed = true)
                                                        is EllipseShape -> mockk<EllipseShape.Ellipse>(relaxed = true)
                                                        is HexagonShape -> mockk<HexagonShape.Hexagon>(relaxed = true)
                                                        is ObbShape -> mockk<ObbShape.OrientedBoundingBox>(relaxed = true)
                                                        is PentagonShape -> mockk<PentagonShape.Pentagon>(relaxed = true)
                                                        is RectangleShape -> mockk<RectangleShape.Rectangle>(relaxed = true)
                                                        is SkewedEllipseShape -> mockk<SkewedEllipseShape.RotatedEllipse>(relaxed = true)
                                                        is SquareShape -> mockk<RectangleShape.Rectangle>(relaxed = true)
                                                        is TriangleShape -> mockk<TriangleShape.Triangle>(relaxed = true)
                                                    },
                                                ),
                                            ),
                            ),
                        )
                    }.flatten()
            return allTestData.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}
