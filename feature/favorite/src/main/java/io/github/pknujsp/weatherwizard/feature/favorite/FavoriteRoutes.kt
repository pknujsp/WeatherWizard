package io.github.pknujsp.weatherwizard.feature.favorite

import io.github.pknujsp.weatherwizard.core.ui.ParentRoutes
import io.github.pknujsp.weatherwizard.core.ui.Routes

sealed interface FavoriteRoutes : Routes {

    companion object : ParentRoutes {
        override val route: String = "Favorite"
        override val navIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_baseline_map_24
        override val navTitle: Int = io.github.pknujsp.weatherwizard.core.common.R.string.nav_favorite_areas
    }

    data object FavoriteAreaList : FavoriteRoutes {
        override val route: String = "Favorite/FavoriteAreaList"
    }

    data object AreaSearch : FavoriteRoutes {
        override val route: String = "Favorite/AreaSearch"
    }
}