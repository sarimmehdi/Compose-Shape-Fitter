package com.sarim.screenshot_drawing_screen_type4

import android.view.LayoutInflater
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenDataProviderType4
import com.sarim.nav.theme.ComposeShapeFitterSampleAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import com.sarim.screenshot_drawing_screen_type4.databinding.ComposeViewBinding
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean

internal data class TestDataDrawingScreenPreviewType4ScreenshotTest(
    val data: DrawingScreenData,
    val showSnackbar: Boolean,
)

@RunWith(Parameterized::class)
internal class DrawingScreenPreviewType4ScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataDrawingScreenPreviewType4ScreenshotTest,
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
                    ComposeShapeFitterSampleAppTheme {
                        DrawingScreen(
                            data =
                                testData.data.copy(
                                    state =
                                        testData.data.state.copy(
                                            inPreviewMode = true,
                                        ),
                                ),
                        )
                        if (testData.showSnackbar) {
                            LaunchedEffect(Unit) {
                                testData.data.snackbarHostState.showSnackbar(
                                    message = "Error",
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
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
        fun getParameters(): Collection<Array<Any>> {
            var overallIndex = 0
            return DrawingScreenDataProviderType4()
                .values
                .flatMap { drawingData ->
                    Exhaustive.boolean().values.map { showSnackbarValue ->
                        val currentTestIndex = overallIndex++
                        arrayOf(
                            currentTestIndex.toString(),
                            TestDataDrawingScreenPreviewType4ScreenshotTest(
                                data = drawingData,
                                showSnackbar = showSnackbarValue,
                            ),
                        )
                    }
                }.toList()
        }
    }
}
