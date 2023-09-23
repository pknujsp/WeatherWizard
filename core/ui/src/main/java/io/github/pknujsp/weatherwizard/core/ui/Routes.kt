package io.github.pknujsp.weatherwizard.core.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

interface Routes {
    val route: String
}

interface ParentRoutes {
    val route: String
    val navIcon: Int
    val navTitle: Int
}