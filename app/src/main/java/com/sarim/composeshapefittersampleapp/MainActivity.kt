package com.sarim.composeshapefittersampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.sarim.composeshapefittersampleapp.ui.theme.ComposeShapeFitterSampleAppTheme
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenNavigationDestination
import com.sarim.example_app_presentation.DrawingScreenViewModel
import com.sarim.utils.ObserveAsEvents
import com.sarim.utils.SnackBarController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.LocalKoinScope
import org.koin.compose.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

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

                MainActivityScreen(
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun MainActivityScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) {
    val navController = rememberNavController()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DrawingFeature,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation<DrawingFeature>(startDestination = DrawingScreenNavigationDestination) {
                composable<DrawingScreenNavigationDestination> { backStackEntry ->
                    val koin = getKoin()
                    val drawingFeatureScope: Scope = remember(backStackEntry) {
                        koin.getOrCreateScope(backStackEntry.id, named(DrawingFeature::class.java.name))
                    }
                    val drawingScreenViewModel = koinViewModel<DrawingScreenViewModel>(
                        viewModelStoreOwner = backStackEntry,
                        scope = drawingFeatureScope
                    )
                    val drawingScreenState by drawingScreenViewModel.state.collectAsStateWithLifecycle()
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    DrawingScreen(
                        data =
                            DrawingScreenData(
                                state = drawingScreenState,
                                drawerState = drawerState,
                            ),
                        onEvent = drawingScreenViewModel::onEvent,
                    )
                }
            }
        }
    }
}

@Serializable
data object DrawingFeature
