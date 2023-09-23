package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.favorite.search.SearchAreaScreen


@Composable
fun HostFavoriteScreen() {
    val navController = rememberNavController()
    NavHost(modifier = Modifier.navigationBarsPadding(), navController = navController, route = FavoriteRoutes.route, startDestination =
    FavoriteRoutes
        .FavoriteAreaList.route) {
        composable(FavoriteRoutes.FavoriteAreaList.route) { FavoriteAreaListScreen(navController) }
        composable(FavoriteRoutes.AreaSearch.route) { SearchAreaScreen(navController) }
    }
}