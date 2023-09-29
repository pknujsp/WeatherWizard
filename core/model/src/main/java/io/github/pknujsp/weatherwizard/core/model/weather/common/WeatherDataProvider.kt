package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed interface WeatherDataProvider {
    val name: Int
    val logo: Int

    data object Kma : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.kmaicon
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.kma
    }

    data object MetNorway : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.metlogo
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.met_norway
    }
}