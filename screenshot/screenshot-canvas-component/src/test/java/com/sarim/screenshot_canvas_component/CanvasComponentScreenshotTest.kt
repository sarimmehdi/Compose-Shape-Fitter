package com.sarim.screenshot_canvas_component

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.component.CanvasComponent
import com.sarim.example_app_presentation.component.CanvasComponentData
import com.sarim.example_app_presentation.component.CanvasComponentDataParameterProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataCanvasComponentScreenshotTest(
    val data: CanvasComponentData,
)

@RunWith(Parameterized::class)
class CanvasComponentScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataCanvasComponentScreenshotTest,
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_6_PRO,
        )

    @Test
    fun test() {
        paparazzi.snapshot {
            CanvasComponent(
                data = testData.data,
                modifier = Modifier.background(Color.White),
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
            CanvasComponentDataParameterProvider()
                .values
                .mapIndexed { index, data ->
                    arrayOf(
                        index.toString(),
                        TestDataCanvasComponentScreenshotTest(
                            data = data,
                        ),
                    )
                }.toList()
    }
}
