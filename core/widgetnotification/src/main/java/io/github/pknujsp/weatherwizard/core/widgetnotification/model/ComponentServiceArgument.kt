package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import androidx.core.os.bundleOf
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor


abstract class ComponentServiceArgument {

    private val parametersInConstructor = this::class.primaryConstructor!!.parameters.map { it.name!! }.toSet()

    fun toMap() = this::class.declaredMemberProperties.filter { it.name in parametersInConstructor }.associate {
        it.name to it.getter.call(this)!!
    }.plus("KEY" to this::class.simpleName!!)

    fun toBundle() = bundleOf(*this@ComponentServiceArgument::class.declaredMemberProperties.filter {
        it.name in parametersInConstructor
    }.map { it.name to it.getter.call(this)!! }.plus("KEY" to this::class.simpleName!!).toTypedArray())
}

class OngoingNotificationServiceArgument : ComponentServiceArgument()

class DailyNotificationServiceArgument(
    val notificationId: Long
) : ComponentServiceArgument()


class WidgetUpdatedArgument(
    val action: Int, val widgetIds: Array<Int> = emptyArray(),
) : ComponentServiceArgument() {
    companion object {
        const val UPDATE_ALL = 0
        const val UPDATE_ONLY_SPECIFIC_WIDGETS = 1
        const val SCHEDULE_TO_AUTO_REFRESH = 2
        const val DELETE = 3
    }
}

class LoadWidgetDataArgument(
    val action: Int, val widgetId: Int = 0
) : ComponentServiceArgument() {
    companion object {
        const val NEW_WIDGET = 0
        const val UPDATE_ALL = 2
        const val UPDATE_ONLY_FAILED = 3
    }
}