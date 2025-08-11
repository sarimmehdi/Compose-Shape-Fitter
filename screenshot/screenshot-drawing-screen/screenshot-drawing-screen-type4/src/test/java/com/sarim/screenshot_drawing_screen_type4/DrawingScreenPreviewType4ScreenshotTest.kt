package com.sarim.screenshot_drawing_screen_type4

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenDataProviderType4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenPreviewType4ScreenshotTest(
    val data: DrawingScreenData,
)

@RunWith(Parameterized::class)
class DrawingScreenPreviewType4ScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenPreviewType4ScreenshotTest,
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_6_PRO,
        )

    @Test
    fun test() {
        paparazzi.snapshot {
            DrawingScreen(
                data =
                    testData.data.copy(
                        state =
                            testData.data.state.copy(
                                inPreviewMode = true,
                            ),
                    ),
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> =
            DrawingScreenDataProviderType4()
                .values
                .mapIndexed { index, data ->
                    arrayOf(
                        index.toString(),
                        TestDataDrawingScreenPreviewType4ScreenshotTest(
                            data = data,
                        ),
                    )
                }.toList()
    }
}
