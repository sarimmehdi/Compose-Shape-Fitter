package com.sarim.example_app_presentation.component

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import com.sarim.example_app_domain.model.Shape
import com.sarim.example_app_presentation.DrawingScreenToViewModelEvents
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.LAZY_COLUMN
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.SELECTED_NAVIGATION_DRAWER_ITEM
import com.sarim.example_app_presentation.component.DrawerComponentTestTags.UNSELECTED_NAVIGATION_DRAWER_ITEM
import io.mockk.mockk
import io.mockk.verifyOrder
import junit.framework.TestCase.fail
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

data class TestDataDrawerComponentTestSelectShape(
    val selectedShape: Shape,
    val shapeToClickOn: Shape,
) {
    val testDescription =
        "$selectedShape must be selected and you must click on $shapeToClickOn to close drawer"
}

@RunWith(ParameterizedRobolectricTestRunner::class)
class DrawerComponentTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawerComponentTestSelectShape,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    val onEvent: (DrawingScreenToViewModelEvents) -> Unit = mockk(relaxed = true)
    val allShapes = Shape.entries.toTypedArray().toImmutableList()

    lateinit var selectedShapeString: String
    lateinit var shapeToClickOnString: String
    lateinit var allShapeStrings: List<String>

    @Test
    fun test() {
        composeTestRule.setContent {
            selectedShapeString = stringResource(testData.selectedShape.shapeStringId)
            shapeToClickOnString = stringResource(testData.shapeToClickOn.shapeStringId)
            allShapeStrings = allShapes.map { stringResource(it.shapeStringId) }
            DrawerComponent(
                data =
                    DrawerComponentData(
                        allShapes = allShapes,
                        selectedShape = testData.selectedShape,
                    ),
                onEvent = onEvent,
            )
        }

        if (::selectedShapeString.isInitialized && ::allShapeStrings.isInitialized) {
            composeTestRule
                .onNodeWithTag(LAZY_COLUMN)
                .performScrollToNode(hasTestTag(SELECTED_NAVIGATION_DRAWER_ITEM + selectedShapeString))
            composeTestRule
                .onNodeWithTag(LAZY_COLUMN)
                .performScrollToIndex(0)

            repeat(allShapes.size) { i ->
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToIndex(i)
                composeTestRule
                    .onNodeWithTag(
                        UNSELECTED_NAVIGATION_DRAWER_ITEM + selectedShapeString,
                    ).assertDoesNotExist()
            }
            composeTestRule
                .onNodeWithTag(LAZY_COLUMN)
                .performScrollToIndex(0)

            allShapeStrings.filter { it != selectedShapeString }.forEach {
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToNode(hasTestTag(UNSELECTED_NAVIGATION_DRAWER_ITEM + it))
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToIndex(0)

                repeat(allShapes.size) { i ->
                    composeTestRule
                        .onNodeWithTag(LAZY_COLUMN)
                        .performScrollToIndex(i)
                    composeTestRule
                        .onNodeWithTag(
                            SELECTED_NAVIGATION_DRAWER_ITEM + it,
                        ).assertDoesNotExist()
                }
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToIndex(0)
            }

            if (testData.shapeToClickOn == testData.selectedShape) {
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToNode(hasTestTag(SELECTED_NAVIGATION_DRAWER_ITEM + shapeToClickOnString))
                composeTestRule
                    .onNodeWithTag(
                        SELECTED_NAVIGATION_DRAWER_ITEM + shapeToClickOnString,
                    ).performClick()
            } else {
                composeTestRule
                    .onNodeWithTag(LAZY_COLUMN)
                    .performScrollToNode(hasTestTag(UNSELECTED_NAVIGATION_DRAWER_ITEM + shapeToClickOnString))
                composeTestRule
                    .onNodeWithTag(
                        UNSELECTED_NAVIGATION_DRAWER_ITEM + shapeToClickOnString,
                    ).performClick()
            }

            composeTestRule.runOnIdle {
                verifyOrder {
                    onEvent(
                        eq(
                            DrawingScreenToViewModelEvents.SetSelectedShape(
                                testData.shapeToClickOn,
                            ),
                        ),
                    )
                }
            }
        } else {
            if (!::selectedShapeString.isInitialized) {
                fail("failed to initialize lateinit variable selectedShapeString")
            }
            if (!::allShapeStrings.isInitialized) {
                fail("failed to initialize lateinit variable allShapeStrings")
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> =
            Shape.entries
                .flatMap { selectedShape ->
                    Shape.entries.map { shapeToClickOn ->
                        TestDataDrawerComponentTestSelectShape(
                            selectedShape = selectedShape,
                            shapeToClickOn = shapeToClickOn,
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
