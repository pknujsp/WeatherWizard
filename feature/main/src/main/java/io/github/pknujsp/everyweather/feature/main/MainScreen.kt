package io.github.pknujsp.everyweather.feature.main

import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ads.AdMob
import io.github.pknujsp.everyweather.core.ui.MainRoutes
import io.github.pknujsp.everyweather.core.ui.RootNavControllerViewModel
import io.github.pknujsp.everyweather.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.everyweather.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.everyweather.feature.main.exit.AppCloseDialog
import io.github.pknujsp.everyweather.feature.main.sidebar.favorites.FavoriteLocationsScreen
import io.github.pknujsp.everyweather.feature.settings.HostSettingsScreen
import io.github.pknujsp.everyweather.feature.splash.OnboardingScreen
import io.github.pknujsp.everyweather.feature.weather.HostWeatherScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState = rememberMainState(rootNavControllerViewModel.requestedRoute)
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    var isCloseAppDialogVisible by remember { mutableStateOf(false) }
    val currentCloseAppDialogVisible by rememberUpdatedState(newValue = isCloseAppDialogVisible)
    val isInitialized = mainViewModel.isInitialized

    if (isInitialized != null) {
        val destination by remember {
            derivedStateOf { if (isInitialized) MainRoutes.Weather.route else MainRoutes.Onboarding.route }
        }

        LaunchedEffect(Unit) {
            onBackPressedDispatcherOwner!!.onBackPressedDispatcher.addCallback(lifeCycleOwner) {
                mainUiState.navController.run {
                    if (currentBackStackEntry != null && currentBackStackEntry!!.destination.route == destination) {
                        isCloseAppDialogVisible = true
                    } else {
                        popBackStack()
                    }
                }
            }
        }

        if (currentCloseAppDialogVisible) {
            AppCloseDialog {
                isCloseAppDialogVisible = false
            }
        }

        NavHost(
            navController = mainUiState.navController,
            startDestination = destination,
            route = MainRoutes.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(MainRoutes.Weather.route) {
                WeatherMainScreen(mainUiState)
            }
            composable(MainRoutes.Favorite.route) {
                HostFavoriteScreen()
            }
            composable(MainRoutes.Notification.route) {
                HostNotificationScreen()
            }
            composable(MainRoutes.Settings.route) {
                HostSettingsScreen()
            }
            composable(MainRoutes.Onboarding.route) {
                OnboardingScreen {
                    mainUiState.navigate(MainRoutes.Weather, true)
                }
            }
        }
    }
}

@Composable
private fun WeatherMainScreen(mainUiState: MainUiState) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet(drawerContainerColor = Color.White, modifier = Modifier.systemBarsPadding()) {
            Spacer(modifier = Modifier.height(24.dp))
            FavoriteLocationsScreen(closeDrawer = {
                scope.launch {
                    drawerState.close()
                }
            })
            mainUiState.tabs.values.forEach {
                DrawerRouteItem(it) {
                    scope.launch {
                        mainUiState.navigate(it)
                        drawerState.close()
                    }
                }
            }
            AdMob.BannerAd(
                modifier =
                    Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
            )
            DrawerFooter()
        }
    }) {
        HostWeatherScreen {
            scope.launch {
                drawerState.open()
            }
        }
    }
}

@Composable
private fun DrawerRouteItem(
    route: MainRoutes,
    onClick: () -> Unit,
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = stringResource(id = route.navTitle),
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = route.navIcon),
                contentDescription = stringResource(id = route.navTitle),
            )
        },
        selected = false,
        onClick = { onClick() },
    )
}

@Composable
private fun DrawerFooter() {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp),
    ) {
        AsyncImage(
            modifier = Modifier.height(20.dp),
            model =
                ImageRequest.Builder(LocalContext.current).data(io.github.pknujsp.everyweather.core.resource.R.drawable.textlogo_small)
                    .build(),
            contentDescription = null,
        )
    }
}