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


data class DailyNotificationServiceArgument(
    val notificationId: Long
) : ComponentServiceArgument()


data class WidgetServiceArgument(
    val action: String,
    val widgetIds: Array<Int>,
) : ComponentServiceArgument() {
    val actionType: ComponentServiceAction.Widget.WidgetAction
        get() = ComponentServiceAction.Widget.WidgetAction.valueOf(action)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetServiceArgument

        if (action != other.action) return false
        return widgetIds.contentEquals(other.widgetIds)
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + widgetIds.contentHashCode()
        return result
    }
}