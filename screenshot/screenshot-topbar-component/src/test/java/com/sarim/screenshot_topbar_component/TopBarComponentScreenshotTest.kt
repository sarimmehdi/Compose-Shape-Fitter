package com.sarim.screenshot_topbar_component

import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.sarim.example_app_presentation.component.TopBarComponent
import com.sarim.example_app_presentation.component.TopBarComponentData
import com.sarim.example_app_presentation.component.TopBarComponentDataProvider
import com.sarim.nav.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.screenshot_topbar_component.databinding.ComposeViewBinding
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

internal data class TestDataTopBarComponentScreenshotTest(
    val data: TopBarComponentData,
)

@RunWith(Parameterized::class)
internal class CanvasComponentScreenshotTest(
    @Suppress("UNUSED_PARAMETER") private val testDescription: String,
    private val testData: TestDataTopBarComponentScreenshotTest,
) {
    @get:Rule
    val paparazzi = Paparazzi()

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
                        TopBarComponent(
                            data = testData.data,
                            modifier =
                                Modifier
                                    .background(Color.White)
                                    .fillMaxSize(),
                        )
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
        fun getParameters(): Collection<Array<Any>> =
            TopBarComponentDataProvider()
                .values
                .mapIndexed { index, data ->
                    arrayOf(
                        index.toString(),
                        TestDataTopBarComponentScreenshotTest(
                            data = data,
                        ),
                    )
                }.toList()
    }
}
