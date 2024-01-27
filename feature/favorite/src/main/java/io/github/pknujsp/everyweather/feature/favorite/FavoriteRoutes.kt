package io.github.pknujsp.everyweather.feature.favorite

import io.github.pknujsp.everyweather.core.ui.Routes

sealed interface FavoriteRoutes : Routes {

    companion object : Routes {
        override val route: String = "Favorite"
    }

    data object FavoriteAreaList : FavoriteRoutes {
        override val route: String = "Favorite/FavoriteAreaList"
    }

    data object AreaSearch : FavoriteRoutes {
        override val route: String = "Favorite/AreaSearch"
    }
}