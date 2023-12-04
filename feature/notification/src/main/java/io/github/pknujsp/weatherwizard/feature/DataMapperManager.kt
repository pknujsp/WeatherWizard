package io.github.pknujsp.weatherwizard.feature

import io.github.pknujsp.weatherwizard.core.common.enum.IEnum

interface DataMapperManager<T : IEnum> {
    fun getMapperByType(type: T): WeatherDataMapper
}