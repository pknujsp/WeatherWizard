package io.github.pknujsp.weatherwizard.feature.main

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, showSystemUi = true, apiLevel = 33, backgroundColor = 0xFFFFFFFF)
@Composable
fun MainScreen() {
    val rootNavControllerViewModel: RootNavControllerViewModel =
        hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))
    val scope = rememberCoroutineScope()
    val tabs: Array<Pair<MainRoutes, @Composable () -> Unit>> = remember {
        arrayOf(MainRoutes.Weather to { HostWeatherScreen() },
            MainRoutes.Favorite to { HostFavoriteScreen() },
            MainRoutes.Notification to { HostNotificationScreen() },
            MainRoutes.Settings to { HostSettingsScreen() })
    }
    val tabIndices = remember {
        tabs.mapIndexed { index, route -> route.first to index }.toMap()
    }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    val window = (LocalContext.current as Activity).window
    val windowInsetController = remember { WindowCompat.getInsetsController(window, window.decorView) }

    LaunchedEffect(Unit) {
        scope.launch {
            rootNavControllerViewModel.requestedRoute.collect { newRoute ->
                pagerState.animateScrollToPage(tabIndices[newRoute]!!)
            }
        }
    }
    LaunchedEffect(selectedTabIndex.value) {
        Log.d("MainScreen", "selectedTabIndex: ${selectedTabIndex.value}")
    }

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {

    }) { _ ->
        Box {
            if (rootNavControllerViewModel.imageUrl != null) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    model = ImageRequest.Builder(LocalContext.current).run {
                        crossfade(200)
                        data(rootNavControllerViewModel.imageUrl)
                        build()
                    },
                    contentDescription = stringResource(R.string.background_image),
                    filterQuality = FilterQuality.High,
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerState,
                    beyondBoundsPageCount = tabs.size,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) { page ->
                    Box(modifier = Modifier.run {
                        statusBarsPadding()
                    }) {
                        tabs[page].second()
                    }
                }
                TabRow(selectedTabIndex = selectedTabIndex.value, modifier = Modifier.fillMaxWidth(), containerColor = Color.Transparent) {
                    tabs.forEachIndexed { index, currentTab ->
                        Tab(selected = selectedTabIndex.value == index,
                            selectedContentColor = Color.Black,
                            unselectedContentColor = Color(0xFF666666),
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = stringResource(id = currentTab.first.navTitle)) })
                    }
                }
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }


}


@Composable
private fun TopNavBar(
    backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Row(modifier = Modifier
        .background(Color.White)
        .padding(start = 12.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        TopNavBarItem(MainRoutes.Weather, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Favorite, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Notification, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Settings, backStackEntry, navController)
    }
}

@Composable
private fun TopNavBarItem(
    route: MainRoutes, backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    TextButton(onClick = {
        if (backStackEntry?.destination?.route != route.route) {
            navController.navigate(route.route) {
                launchSingleTop = true
                backStackEntry?.destination?.route?.let {
                    popUpTo(it) {
                        inclusive = true
                    }
                }
            }
        }
    },
        border = null,
        contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 4.dp, bottom = 14.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = if (backStackEntry?.destination?.route == route.route) Color.Black else Color(
            0xFF666666), containerColor = Color.Transparent)) {
        Text(text = stringResource(id = route.navTitle),
            fontSize = 19.sp,
            fontWeight = FontWeight(400),
            letterSpacing = (-1).sp,
            lineHeight = 24.sp,
            textDecoration = if (backStackEntry?.destination?.route == route.route) TextDecoration.Underline else TextDecoration.None)
    }
}