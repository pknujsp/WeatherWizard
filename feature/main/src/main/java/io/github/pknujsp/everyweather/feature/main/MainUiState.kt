package io.github.pknujsp.everyweather.feature.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.ui.MainRoutes
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setNavigationBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setStatusBarContentColor

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Stable
interface MainUiState {
    val tabs: Map<String, MainRoutes>
    val navController: NavHostController
    fun navigate(route: MainRoutes, force: Boolean = false)
}

private class MutableMainUiState(
    override val tabs: Map<String, MainRoutes>,
    override val navController: NavHostController,
) : MainUiState {
    override fun navigate(route: MainRoutes, force: Boolean) {
        val backStackEntry = navController.currentBackStackEntry
        if (!force && (backStackEntry?.destination?.route == route.route)) {
            return
        }

        navController.navigate(route.route) {
            launchSingleTop = true
            backStackEntry?.destination?.route?.let {
                popUpTo(it) {
                    inclusive = false || route is MainRoutes.Weather
                }
            }
        }
    }
}

@Composable
fun rememberMainState(requestedRoutes: SharedFlow<MainRoutes>, navController: NavHostController = rememberNavController()): MainUiState {
    val tabs = remember {
        mapOf(MainRoutes.Favorite.route to MainRoutes.Favorite,
            MainRoutes.Settings.route to MainRoutes.Settings,
            MainRoutes.Notification.route to MainRoutes.Notification)
    }
    val window = LocalContext.current.asActivity()!!.window
    val windowInsetsControllerCompat = remember(window) {
        WindowInsetsControllerCompat(window, window.decorView)
    }
    val state: MainUiState = remember {
        MutableMainUiState(tabs, navController)
    }

    LaunchedEffect(Unit) {
        launch {
            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                windowInsetsControllerCompat.run {
                    setStatusBarContentColor(SystemBarContentColor.BLACK)
                    setNavigationBarContentColor(SystemBarContentColor.BLACK)
                }
            }
        }

        launch {
            requestedRoutes.collect { newRoute ->
                if (navController.currentBackStackEntry?.destination?.route != newRoute.route) {
                    state.navigate(newRoute)
                }
            }
        }
    }

    return state
}