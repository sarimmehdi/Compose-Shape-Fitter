package com.sarim.composeshapefittersampleapp.presentation.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.NEXUS_6
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.flatMap
import io.kotest.property.exhaustive.map

@Composable
@Preview(
    apiLevel = 35,
    device = NEXUS_6
)
fun TopBarComponentPreview(
    @PreviewParameter(TopBarComponentDataProvider::class) data: TopBarComponentData
) {
    ComposeShapeFitterSampleAppTheme {
        TopBarComponent(
            data = data,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

class TopBarComponentDataProvider : PreviewParameterProvider<TopBarComponentData> {
    override val values: Sequence<TopBarComponentData> =
        Exhaustive.boolean().flatMap { showSettingsDropDown ->
            Exhaustive.boolean().flatMap { showFingerTracedLines ->
                Exhaustive.boolean().flatMap { showApproximatedShape ->
                    exhaustive(DrawerValue.entries).map { drawerValue ->
                        TopBarComponentData(
                            showSettingsDropDown = showSettingsDropDown,
                            showFingerTracedLines = showFingerTracedLines,
                            showApproximatedShape = showApproximatedShape,
                            currentDrawerState = DrawerState(drawerValue)
                        )
                    }
                }
            }
        }.values.asSequence()
    }
