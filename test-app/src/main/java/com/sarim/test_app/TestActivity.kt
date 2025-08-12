package com.sarim.test_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.sarim.nav.MainActivityScreen
import com.sarim.nav.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.utils.ObserveAsEvents
import com.sarim.utils.SnackBarController
import kotlinx.coroutines.launch

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeShapeFitterSampleAppTheme {
                val snackbarHostState =
                    remember {
                        SnackbarHostState()
                    }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                ObserveAsEvents(
                    flow = SnackBarController.events,
                    snackbarHostState,
                ) { event ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        val result =
                            snackbarHostState.showSnackbar(
                                message = event.message.asString(context),
                                actionLabel = event.action?.name?.asString(context),
                                duration = SnackbarDuration.Long,
                            )

                        if (result == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                    }
                }

                MainActivityScreen(
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}