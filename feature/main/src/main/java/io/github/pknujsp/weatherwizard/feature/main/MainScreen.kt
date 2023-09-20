package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.feature.favorite.FavoriteRoutes
import io.github.pknujsp.weatherwizard.feature.favorite.favoriteAreaGraph
import io.github.pknujsp.weatherwizard.feature.settings.SettingsRoutes
import io.github.pknujsp.weatherwizard.feature.settings.settingsGraph
import io.github.pknujsp.weatherwizard.feature.weather.WeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.mainWeatherGraph


@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(bottomBar = {
        BottomNavigationBar(backStackEntry, navController)
    }) { paddingValues ->
        NavHost(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = navController, route = "Root",
            startDestination = WeatherRoutes.route) {
            mainWeatherGraph(navController)
            favoriteAreaGraph(navController)
            settingsGraph()
        }
    }
}


@Composable
private fun BottomNavigationBar(
    backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Row(modifier = Modifier
        .background(Color.White)
        .navigationBarsPadding()
        .padding(vertical = 8.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround) {
        BottomNavigationBarItem(route = WeatherRoutes, backStackEntry, navController)
        BottomNavigationBarItem(route = FavoriteRoutes, backStackEntry, navController)
        BottomNavigationBarItem(route = SettingsRoutes, backStackEntry, navController)
    }
}

@Composable
private fun RowScope.BottomNavigationBarItem(
    route: ParentRoutes, backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Box(modifier = Modifier
        .weight(1f)
        .clip(CircleShape)
        .background(Color.Transparent)
        .clickable(onClick = {
            navController.navigate(route.route) {
                restoreState = true
                launchSingleTop = true
            }
        }),
        contentAlignment = Alignment.Center) {
        Icon(painter = painterResource(id = route.navIcon),
            contentDescription = stringResource(id = route.navTitle),
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp),
            tint = if (backStackEntry?.destination?.route == route.route) Color.Blue else Color.Gray)
    }
}