package com.sarim.screenshot_drawing_screen_type1

import android.view.LayoutInflater
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenDataProviderType1
import com.sarim.screenshot_drawing_screen_type1.databinding.ComposeViewBinding
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class TestDataDrawingScreenPreviewType1ScreenshotTest(
    val data: DrawingScreenData,
)

@RunWith(Parameterized::class)
class DrawingScreenPreviewType1ScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenPreviewType1ScreenshotTest,
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
            DrawingScreenDataProviderType1()
                .values
                .mapIndexed { index, data ->
                    arrayOf(
                        index.toString(),
                        TestDataDrawingScreenPreviewType1ScreenshotTest(
                            data = data,
                        ),
                    )
                }.toList()
    }
}
