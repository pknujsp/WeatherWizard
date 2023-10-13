package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.IEnum

sealed interface WeatherDataProvider : IEnum {
    val name: Int
    val logo: Int

    companion object : BaseEnum<WeatherDataProvider> {
        override val enums: Array<WeatherDataProvider> get() = arrayOf(Kma, MetNorway)
        override val key: String get() = "WeatherDataProvider"
        override val default: WeatherDataProvider get() = Kma
    }

    data object Kma : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.kmaicon
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.kma
        override val key = 0
        override val title: Int = name
    }

    data object MetNorway : WeatherDataProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.metlogo
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.met_norway
        override val key = 1
        override val title: Int = name
    }


}