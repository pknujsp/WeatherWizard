package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.asActivity
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen
import kotlinx.coroutines.flow.SharedFlow

@Stable
interface MainUiState {
    val tabs: Array<Pair<MainRoutes, @Composable () -> Unit>>
    @OptIn(ExperimentalFoundationApi::class) val pagerState: PagerState
    val selectedTabIndex: State<Int>
    val tabIndices: Map<MainRoutes, Int>
}

private class MutableMainUiState @OptIn(ExperimentalFoundationApi::class) constructor(
    override val tabs: Array<Pair<MainRoutes, @Composable () -> Unit>>,
    @OptIn(ExperimentalFoundationApi::class) override val pagerState: PagerState,
    override val selectedTabIndex: State<Int>,
) : MainUiState {
    override val tabIndices = tabs.mapIndexed { index, route -> route.first to index }.toMap()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberMainState(requestedRoutes: SharedFlow<MainRoutes>): MainUiState {
    val tabs: Array<Pair<MainRoutes, @Composable () -> Unit>> = remember {
        arrayOf(MainRoutes.Weather to { HostWeatherScreen() },
            MainRoutes.Favorite to { HostFavoriteScreen() },
            MainRoutes.Notification to { HostNotificationScreen() },
            MainRoutes.Settings to { HostSettingsScreen() })
    }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    val state: MainUiState = remember {
        MutableMainUiState(tabs, pagerState, selectedTabIndex)
    }

    val window = (LocalContext.current.asActivity())!!.window
    val windowInsetController = remember { WindowCompat.getInsetsController(window, window.decorView) }

    LaunchedEffect(Unit) {
        requestedRoutes.collect { newRoute ->
            pagerState.animateScrollToPage(state.tabIndices[newRoute]!!)
        }
    }

    LaunchedEffect(selectedTabIndex.value) {
        val appearance = tabs[selectedTabIndex.value].first.isAppearanceLightSystemBars
        windowInsetController.run {
            isAppearanceLightStatusBars = appearance
            isAppearanceLightNavigationBars = appearance
        }
    }

    return state
}