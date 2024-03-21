package io.github.pknujsp.everyweather.core.ui.route

import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

interface Routes {
    val route: String
}

abstract class RoutesWithArgument(private val baseRoute: String) : Routes {
    abstract val arguments: List<NamedNavArgument>

    override val route: String =
        StringBuilder().run {
            append(baseRoute)
            arguments.forEach { argument ->
                append("/{${argument.name}}")
            }
            toString()
        }

    fun argumentsWithDefaultValue(vararg values: Any): List<NamedNavArgument> =
        arguments.mapIndexed { index, it ->
            navArgument(it.name) {
                type = it.argument.type
                defaultValue = values[index]
            }
        }

    fun routeWithArguments(vararg values: Any): String =
        StringBuilder().run {
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

    @get:StringRes val navTitle: Int
}

interface NestedParentRoutes {
    val route: String
    val startDestination: NestedRoutes
    val routes: Array<NestedRoutes>

    fun <T : NestedRoutes> getRoute(route: String): T {
        return routes.first { it.route == route } as T
    }
}
