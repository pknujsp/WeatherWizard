package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed interface WeatherDataProvider {
    val name: Int
    val logo: Int
    val key :String

    companion object {
        val providers: Array<WeatherDataProvider> get() = arrayOf(Kma, MetNorway)
        const val key :String = "weather_data_provider"
        val default: WeatherDataProvider get() = Kma
    }

    data object Kma : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.kmaicon
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.kma
        override val key: String = "kma"
    }

    data object MetNorway : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.metlogo
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.met_norway
        override val key: String = "met_norway"
    }


}