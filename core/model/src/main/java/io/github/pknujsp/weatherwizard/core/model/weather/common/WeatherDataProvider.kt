package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.IRadioButton

sealed interface WeatherDataProvider : IRadioButton {
    val name: Int
    val logo: Int
    val key: String

    companion object {
        val providers: Array<WeatherDataProvider> get() = arrayOf(Kma, MetNorway)
        const val key: String = "weather_data_provider"
        val default: WeatherDataProvider get() = Kma

        fun fromKey(key: String): WeatherDataProvider {
            return providers.firstOrNull { it.key == key } ?: default
        }
    }

    data object Kma : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.kmaicon
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.kma
        override val key: String = "kma"
        override val title: Int
            get() = name
    }

    data object MetNorway : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.metlogo
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.met_norway
        override val key: String = "met_norway"
        override val title: Int
            get() = name
    }


}