package io.github.pknujsp.weatherwizard.core.common.enum

import kotlin.reflect.KClass

private object PendingIntentRequestFactoryImpl : PendingIntentRequestFactory {
    private val requestIdMap = mutableMapOf<Int, Int>()
    override fun requestId(key: KClass<*>): Int = requestIdMap.getOrPut(key.simpleName.hashCode()) { key.simpleName.hashCode() }

    override fun requestId(key: Int): Int = requestIdMap.getOrPut(key) { key }
}

val pendingIntentRequestFactory: PendingIntentRequestFactory
    get() = PendingIntentRequestFactoryImpl

interface PendingIntentRequestFactory {
    fun requestId(key: KClass<*>): Int
    fun requestId(key: Int): Int
}