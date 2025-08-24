package com.sarim.example_app_presentation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter

@Composable
@Preview(
    apiLevel = 35,
)
internal fun DrawingScreenPreviewType2Snackbar(
    @PreviewParameter(DrawingScreenDataProviderType2::class) data: DrawingScreenData,
) {
    DrawingScreen(
        data =
            data.copy(
                state =
                    data.state.copy(
                        inPreviewMode = true,
                    ),
            ),
    )
    LaunchedEffect(Unit) {
        data.snackbarHostState.showSnackbar(
            message = "Error",
            actionLabel = "Dismiss",
            duration = SnackbarDuration.Short,
        )
    }
}
