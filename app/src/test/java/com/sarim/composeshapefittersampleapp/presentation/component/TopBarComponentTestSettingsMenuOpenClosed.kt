package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.APPROXIMATED_SHAPE_TRAILING_ICON
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES
import com.sarim.composeshapefittersampleapp.presentation.component.TopBarComponentTestTags.FINGER_TRACED_LINES_TRAILING_ICON
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.collections.map

data class TestDataTopBarComponentSettingsMenu(
    val data: TopBarComponentData,
    val enabledTestTags: List<String>,
    val disabledTestTags: List<String>,
) {
    val testDescription =
        "when input data is $data, \n" +
            "components with these test tags are enabled: $enabledTestTags \n" +
            "components with these test tags are disabled: $disabledTestTags"
}

@RunWith(ParameterizedRobolectricTestRunner::class)
class TopBarComponentTestSettingsMenuOpenClosed(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataTopBarComponentSettingsMenu,
) {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        composeTestRule.setContent {
            TopBarComponent(
                data = testData.data,
            )
        }

        composeTestRule.runOnIdle {
            testData.enabledTestTags.forEach {
                composeTestRule.onNodeWithTag(it, useUnmergedTree = true).assertExists()
            }

            testData.disabledTestTags.forEach {
                composeTestRule.onNodeWithTag(it, useUnmergedTree = true).assertDoesNotExist()
            }
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(
            name = "{0}",
        )
        @Suppress("unused", "LongMethod")
        fun getParameters(): Collection<Array<Any>> =
            Exhaustive
                .boolean()
                .flatMap { showSettingsDropDown ->
                    Exhaustive.boolean().flatMap { showFingerTracedLines ->
                        Exhaustive.boolean().map { showApproximatedShape ->
                            TestDataTopBarComponentSettingsMenu(
                                data =
                                    TopBarComponentData(
                                        showSettingsDropDown = showSettingsDropDown,
                                        showFingerTracedLines = showFingerTracedLines,
                                        showApproximatedShape = showApproximatedShape,
                                    ),
                                enabledTestTags =
                                    if (showSettingsDropDown) {
                                        listOf(
                                            FINGER_TRACED_LINES,
                                            APPROXIMATED_SHAPE,
                                        )
                                        if (showFingerTracedLines) {
                                            listOf(FINGER_TRACED_LINES_TRAILING_ICON)
                                        } else {
                                            emptyList()
                                        } +
                                            if (showApproximatedShape) {
                                                listOf(APPROXIMATED_SHAPE_TRAILING_ICON)
                                            } else {
                                                emptyList()
                                            }
                                    } else {
                                        emptyList()
                                    },
                                disabledTestTags =
                                    if (!showSettingsDropDown) {
                                        listOf(
                                            FINGER_TRACED_LINES,
                                            APPROXIMATED_SHAPE,
                                            FINGER_TRACED_LINES_TRAILING_ICON,
                                            APPROXIMATED_SHAPE_TRAILING_ICON,
                                        )
                                    } else {
                                        emptyList()
                                    } +
                                        if (!showFingerTracedLines) {
                                            listOf(FINGER_TRACED_LINES_TRAILING_ICON)
                                        } else {
                                            emptyList()
                                        } +
                                        if (!showApproximatedShape) {
                                            listOf(APPROXIMATED_SHAPE_TRAILING_ICON)
                                        } else {
                                            emptyList()
                                        },
                            )
                        }
                    }
                }.values
                .map { data ->
                    arrayOf(
                        data.testDescription,
                        data,
                    )
                }
    }
}
