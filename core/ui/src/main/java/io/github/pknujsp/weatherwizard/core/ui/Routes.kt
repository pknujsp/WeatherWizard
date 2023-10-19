package io.github.pknujsp.weatherwizard.core.ui

import androidx.navigation.NamedNavArgument

interface Routes {
    val route: String
}

abstract class RoutesWithArgument(private val baseRoute: String) : Routes {
    abstract val arguments: List<NamedNavArgument>

    override val route: String = StringBuilder().run {
        append(baseRoute)
        arguments.forEach { argument ->
            append("/{${argument.name}}")
        }
        toString()
    }

    fun routeWithArguments(vararg values: Any): String = StringBuilder().run {
        append(baseRoute)
        values.forEach { argument ->
            append("/$argument")
        }
        toString()
    }
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
    fun getRoute(route: String): NestedRoutes {
        return routes.first { it.route == route }
    }
}