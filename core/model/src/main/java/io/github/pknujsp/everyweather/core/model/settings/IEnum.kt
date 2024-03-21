package io.github.pknujsp.everyweather.core.model.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface IEnum {
    @get:StringRes val title: Int

    @get:DrawableRes val icon: Int?
    val key: Int
}

interface BaseEnum<T : IEnum> {
    val default: T
    val enums: Array<T>
    val key: String

    fun fromKey(key: Int): T {
        return enums.firstOrNull { it.key == key } ?: default
    }
}
