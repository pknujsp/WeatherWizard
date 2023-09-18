package io.github.pknujsp.weatherwizard.core.common.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.R


enum class SunSetRise(@StringRes val stringRes: Int, @DrawableRes val iconRes: Int) {
    SUN_RISE(R.string.sun_rise, io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear),
    SUN_SET(R.string.sun_set, io.github.pknujsp.weatherwizard.core.common.R.drawable.night_clear)
}