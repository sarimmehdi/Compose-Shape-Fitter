package com.sarim.nav

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import com.sarim.example_app_presentation.DrawingFeature
import com.sarim.example_app_presentation.DrawingScreen
import com.sarim.example_app_presentation.DrawingScreenData
import com.sarim.example_app_presentation.DrawingScreenNavigationDestination
import com.sarim.example_app_presentation.DrawingScreenViewModel
import com.sarim.utils.ui.ObserveAsEvents
import com.sarim.utils.ui.SnackBarController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

@Composable
fun MainActivityScreen(modifier: Modifier = Modifier) {
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
                    duration = SnackbarDuration.Short,
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
        modifier = modifier,
    ) {
        navigation<DrawingFeature>(startDestination = DrawingScreenNavigationDestination) {
            composable<DrawingScreenNavigationDestination> { backStackEntry ->
                val koin = getKoin()
                val drawingFeatureScope: Scope =
                    remember(backStackEntry) {
                        koin.getOrCreateScope(backStackEntry.id, named(DrawingFeature::class.java.name))
                    }
                val drawingScreenViewModel =
                    koinViewModel<DrawingScreenViewModel>(
                        viewModelStoreOwner = backStackEntry,
                        scope = drawingFeatureScope,
                    )
                val drawingScreenState by drawingScreenViewModel.state.collectAsStateWithLifecycle()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                DrawingScreen(
                    data =
                        DrawingScreenData(
                            state = drawingScreenState,
                            drawerState = drawerState,
                            snackbarHostState = snackbarHostState,
                        ),
                    onEvent = drawingScreenViewModel::onEvent,
                )
            }
        }
    }
}
