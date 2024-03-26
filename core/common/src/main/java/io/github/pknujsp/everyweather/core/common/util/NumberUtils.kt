package io.github.pknujsp.everyweather.core.common.util

import kotlin.math.floor

fun Double.toCoordinate(): Double = floor(this * 1000.0) / 1000.0

fun Double.normalize(): Double = (this * 10).toInt() / 10.0