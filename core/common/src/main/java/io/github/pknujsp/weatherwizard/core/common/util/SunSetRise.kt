package io.github.pknujsp.weatherwizard.core.common.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.resource.R


enum class SunSetRise(@StringRes val stringRes: Int, @DrawableRes val iconRes: Int) {
    SUN_RISE(R.string.sun_rise, R.drawable.ic_weather_clear_day),
    SUN_SET(R.string.sun_set, R.drawable.ic_weather_clear_night)
}