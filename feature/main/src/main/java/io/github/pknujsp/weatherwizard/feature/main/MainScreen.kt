package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.core.ui.main.MainViewModel
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel()
    val mainUiState = rememberMainState(rootNavControllerViewModel.requestedRoute)
    val scope = rememberCoroutineScope()

    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {

    }) { _ ->
        Box {
            if (mainViewModel.imageUrl != null && mainUiState.tabs[mainUiState.pagerState.currentPage].first.isFullScreen) {

            }
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = mainUiState.pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) { page ->
                    Box(modifier = Modifier.statusBarsPadding()) {
                        mainUiState.tabs[page].second()
                    }
                }
                TabRow(
                    selectedTabIndex = mainUiState.selectedTabIndex.value,
                    modifier = Modifier.fillMaxWidth(),
                    divider = {},
                ) {
                    mainUiState.tabs.forEachIndexed { index, currentTab ->
                        Tab(selected = mainUiState.selectedTabIndex.value == index,
                            selectedContentColor = Color.Blue,
                            unselectedContentColor = Color.Gray,
                            onClick = {
                                scope.launch {
                                    mainUiState.pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = {
                                Icon(painter = painterResource(id = currentTab.first.navIcon),
                                    contentDescription = stringResource(id = currentTab.first.navTitle))
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