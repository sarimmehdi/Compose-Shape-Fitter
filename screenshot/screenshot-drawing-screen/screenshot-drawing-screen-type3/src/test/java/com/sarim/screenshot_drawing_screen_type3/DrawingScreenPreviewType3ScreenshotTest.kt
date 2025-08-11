package com.sarim.screenshot_drawing_screen_type3

import android.view.LayoutInflater
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenDataProviderType3
import com.sarim.screenshot_drawing_screen_type3.databinding.ComposeViewBinding
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenPreviewType3ScreenshotTest(
    val data: DrawingScreenData,
)

@RunWith(Parameterized::class)
class DrawingScreenPreviewType3ScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenPreviewType3ScreenshotTest,
) {
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_6_PRO,
        )

    private var binding: ComposeViewBinding? = null

    @Test
    fun test() {
        val inflater = LayoutInflater.from(paparazzi.context)
        binding = ComposeViewBinding.inflate(inflater, null, false)
        binding?.let {
            it.composeView.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
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

            paparazzi.gif(view = it.root)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(
            name = "{0}",
        )
        @Suppress("unused")
        fun getParameters(): Collection<Array<Any>> =
            DrawingScreenDataProviderType3()
                .values
                .mapIndexed { index, data ->
                    arrayOf(
                        index.toString(),
                        TestDataDrawingScreenPreviewType3ScreenshotTest(
                            data = data,
                        ),
                    )
                }.toList()
    }
}
