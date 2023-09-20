package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.favorite.search.SearchAreaScreen


@Composable
fun HostFavoriteScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, route = FavoriteRoutes.route, startDestination = FavoriteRoutes.FavoriteAreaList.route) {
        composable(FavoriteRoutes.FavoriteAreaList.route) { FavoriteAreaListScreen(navController) }
        composable(FavoriteRoutes.AreaSearch.route) { SearchAreaScreen(navController) }
    }
}