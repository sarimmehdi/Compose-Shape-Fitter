package com.sarim.screenshot_drawer_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.component.DrawerComponent
import com.sarim.example_app_presentation.component.DrawerComponentData
import com.sarim.example_app_presentation.component.DrawerComponentDataParameterProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawerComponentScreenshotTest(
    val data: DrawerComponentData,
)

@RunWith(Parameterized::class)
class CanvasComponentScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawerComponentScreenshotTest,
) {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO,
    )

    @Test
    fun test() {
        paparazzi.snapshot {
            DrawerComponent(
                data = testData.data,
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
            )
        }
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> {
            return DrawerComponentDataParameterProvider().values.mapIndexed { index, data ->
                arrayOf(
                    index.toString(),
                    TestDataDrawerComponentScreenshotTest(
                        data = data
                    ),
                )
            }.toList()
        }
    }
}
