package io.github.pknujsp.weatherwizard.core.ui

interface Routes {
    val route: String
}

interface NestedRoutes {
    val route: String
}

interface ParentRoutes {
    val route: String
    val navIcon: Int
    val navTitle: Int
}

interface NestedParentRoutes {
    val route: String
    val startDestination: NestedRoutes
    val routes: Array<NestedRoutes>
    fun getRoute(route: String): NestedRoutes{
        return routes.first { it.route == route }
    }
}