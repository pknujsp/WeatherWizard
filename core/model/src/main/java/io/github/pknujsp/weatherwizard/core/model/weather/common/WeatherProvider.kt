package io.github.pknujsp.weatherwizard.core.model.weather.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.enum.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.enum.IEnum

sealed interface WeatherProvider : IEnum {
    val name: Int
    val logo: Int
    val majorWeatherEntityTypes: Set<MajorWeatherEntityType>

    companion object : BaseEnum<WeatherProvider> {
        override val enums: Array<WeatherProvider> get() = arrayOf(Kma, MetNorway)
        override val key: String get() = "WeatherDataProvider"
        override val default: WeatherProvider get() = Kma
    }

    data object Kma : WeatherProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.kmaicon
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.kma
        override val key = 0
        override val title: Int = name
        override val majorWeatherEntityTypes: Set<MajorWeatherEntityType> = setOf(
            MajorWeatherEntityType.CURRENT_CONDITION,
            MajorWeatherEntityType.HOURLY_FORECAST,
            MajorWeatherEntityType.DAILY_FORECAST,
            MajorWeatherEntityType.YESTERDAY_WEATHER,
        )
    }

    data object MetNorway : WeatherProvider {
        @DrawableRes override val logo: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.metlogo
        @StringRes override val name: Int = io.github.pknujsp.weatherwizard.core.common.R.string.met_norway
        override val key = 1
        override val title: Int = name
        override val majorWeatherEntityTypes: Set<MajorWeatherEntityType> = setOf(
            MajorWeatherEntityType.CURRENT_CONDITION,
            MajorWeatherEntityType.HOURLY_FORECAST,
            MajorWeatherEntityType.DAILY_FORECAST,
        )
    }


}