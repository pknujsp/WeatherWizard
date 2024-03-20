package io.github.pknujsp.everyweather.core.common.util

import kotlin.math.floor

fun Double.toLeastZero(): Double = if (isNaN()) 0.0 else this

fun Double.toCoordinate(): Double = floor(this * 1000.0) / 1000.0
