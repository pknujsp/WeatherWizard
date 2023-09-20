package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.pknujsp.weatherwizard.feature.favorite.search.SearchAreaScreen


fun NavGraphBuilder.favoriteAreaGraph(navController: NavController) {
    navigation(startDestination = FavoriteRoutes.FavoriteAreaList.route, route = FavoriteRoutes.route) {
        composable(FavoriteRoutes.FavoriteAreaList.route) { FavoriteAreaListScreen(navController) }
        composable(FavoriteRoutes.AreaSearch.route) { SearchAreaScreen(navController) }
    }
}