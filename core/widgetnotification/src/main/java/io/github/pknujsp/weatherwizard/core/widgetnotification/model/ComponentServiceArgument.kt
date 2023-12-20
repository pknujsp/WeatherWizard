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

class WidgetDeletedArgument(
    val widgetIds: Array<Int>,
) : ComponentServiceArgument()

class WidgetOptionsChangedArgument(
    val widgetId: Int,
) : ComponentServiceArgument()

class WidgetUpdatedArgument(
    val widgetIds: Array<Int>,
) : ComponentServiceArgument()

class LoadWidgetDataArgument(
    val action: Int, val widgetIds: Array<Int> = emptyArray()
) : ComponentServiceArgument() {
    companion object {
        const val NEW_WIDGET = 0
        const val UPDATE_ONLY_ON_CURRENT_LOCATION = 1
        const val UPDATE_ALL = 2
        const val UPDATE_ONLY_SPECIFIC_WIDGETS = 3
    }
}