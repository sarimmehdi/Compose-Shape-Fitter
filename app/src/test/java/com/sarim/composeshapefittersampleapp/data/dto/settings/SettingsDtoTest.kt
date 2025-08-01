package com.sarim.composeshapefittersampleapp.data.dto.settings

import com.google.common.truth.Truth.assertThat
import com.sarim.composeshapefittersampleapp.domain.model.Settings
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataSettingsDtoTest(
    val input: SettingsDto,
    val expectedOutput: Settings,
) {
    val testDescription =
        "when input is $input, " +
            "the expected output should be $expectedOutput"
}

@RunWith(Parameterized::class)
class SettingsDtoTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataSettingsDtoTest,
) {
    @Test
    fun test() {
        assertThat(testData.input.toSettings()).isEqualTo(testData.expectedOutput)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> {
            val booleanExhaustive: Exhaustive<Boolean> = Exhaustive.boolean()

            val testDataExhaustive =
                booleanExhaustive.flatMap { showFingerTracedLines ->
                    booleanExhaustive.map { showApproximatedShape ->
                        TestDataSettingsDtoTest(
                            input =
                                SettingsDto(
                                    showFingerTracedLines = showFingerTracedLines,
                                    showApproximatedShape = showApproximatedShape,
                                ),
                            expectedOutput =
                                Settings(
                                    showFingerTracedLines = showFingerTracedLines,
                                    showApproximatedShape = showApproximatedShape,
                                ),
                        )
                    }
                }

            return testDataExhaustive.values.map { data ->
                arrayOf(
                    data.testDescription,
                    data,
                )
            }
        }
    }
}
