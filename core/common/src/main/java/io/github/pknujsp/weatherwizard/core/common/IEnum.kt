package io.github.pknujsp.weatherwizard.core.common

interface IEnum {
    val title: Int
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