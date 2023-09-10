package io.github.pknujsp.weatherwizard.core.common.util

fun Double.toLeastZero():Double = if(isNaN()) 0.0 else this