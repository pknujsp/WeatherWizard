package io.github.pknujsp.weatherwizard.core.model.weather.common

import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel

sealed interface WeatherProvider : PreferenceModel {
    val majorWeatherEntityTypes: Set<MajorWeatherEntityType>

    companion object : BasePreferenceModel<WeatherProvider> {
        override val enums: Array<WeatherProvider> get() = arrayOf(Kma, MetNorway)
        override val key: String get() = "WeatherDataProvider"
        override val default: WeatherProvider get() = Kma
    }

    data object Kma : WeatherProvider {
        override val icon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.kmaicon
        override val key = 0
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.kma
        override val majorWeatherEntityTypes: Set<MajorWeatherEntityType> = setOf(
            MajorWeatherEntityType.CURRENT_CONDITION,
            MajorWeatherEntityType.HOURLY_FORECAST,
            MajorWeatherEntityType.DAILY_FORECAST,
            MajorWeatherEntityType.YESTERDAY_WEATHER,
        )
    }

    data object MetNorway : WeatherProvider {
        override val icon: Int = io.github.pknujsp.weatherwizard.core.resource.R.drawable.metlogo
        override val key = 1
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.met_norway
        override val majorWeatherEntityTypes: Set<MajorWeatherEntityType> = setOf(
            MajorWeatherEntityType.CURRENT_CONDITION,
            MajorWeatherEntityType.HOURLY_FORECAST,
            MajorWeatherEntityType.DAILY_FORECAST,
        )
    }


}