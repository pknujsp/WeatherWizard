package io.github.pknujsp.everyweather.core.widgetnotification.model

import androidx.annotation.Keep
import androidx.core.os.bundleOf
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

@Keep
abstract class ComponentServiceArgument {

    private companion object {
        const val KEY = "KEY"
    }

    private val parametersInConstructor = this::class.primaryConstructor!!.parameters.map { it.name!! }.toSet()

    fun toMap() = this::class.declaredMemberProperties.filter { it.name in parametersInConstructor }.associate {
        it.name to it.getter.call(this)!!
    }.plus(KEY to this::class.simpleName!!)

    fun toBundle() = bundleOf(*this@ComponentServiceArgument::class.declaredMemberProperties.filter {
        it.name in parametersInConstructor
    }.map { it.name to it.getter.call(this)!! }.plus(KEY to this::class.simpleName!!).toTypedArray())
}

@Keep
class OngoingNotificationServiceArgument : ComponentServiceArgument()

@Keep
data class DailyNotificationServiceArgument(
    val notificationId: Long
) : ComponentServiceArgument()

@Keep
data class WidgetUpdatedArgument(
    val action: Int, val widgetIds: Array<Int> = emptyArray(),
) : ComponentServiceArgument() {

    companion object {
        const val DRAW = 0
        const val DRAW_ALL = 1
        const val DELETE = 2
        const val DELETE_ALL = 3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetUpdatedArgument

        if (action != other.action) return false
        return widgetIds.contentEquals(other.widgetIds)
    }

    override fun hashCode(): Int {
        var result = action
        result = 31 * result + widgetIds.contentHashCode()
        return result
    }


}

@Keep
data class LoadWidgetDataArgument(
    val action: Int, val widgetId: Int = 0
) : ComponentServiceArgument() {
    companion object {
        const val NEW_WIDGET = 0
        const val UPDATE_ALL = 2
        const val UPDATE_ONLY_FAILED = 3
    }
}