package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreen
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenNavigationDestination
import com.sarim.composeshapefittersampleapp.presentation.DrawingScreenViewModel
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.composeshapefittersampleapp.utils.ObserveAsEvents
import com.sarim.composeshapefittersampleapp.utils.SnackBarController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
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

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = DrawingFeature,
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                ) {
                    navigation<DrawingFeature>(startDestination = DrawingScreenNavigationDestination) {
                        composable<DrawingScreenNavigationDestination> {
                            val drawingScreenViewModel = koinViewModel<DrawingScreenViewModel>()
                            val drawingScreenState by drawingScreenViewModel.state.collectAsStateWithLifecycle()
                            DrawingScreen(
                                state = drawingScreenState,
                                onEvent = drawingScreenViewModel::onEvent,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data object DrawingFeature
