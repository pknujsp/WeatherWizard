package io.github.pknujsp.weatherwizard.core.common.enum

import androidx.annotation.StringRes

interface IEnum {
    @get:StringRes val title: Int
    val key: Int
}

interface BaseEnum<T : IEnum> {
    val default: T
    val key: String
    val enums: Array<T>
    fun fromKey(key: Int): T {
        return enums.firstOrNull { it.key == key } ?: default
    }
}