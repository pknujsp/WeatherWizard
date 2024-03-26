package io.github.pknujsp.everyweather.feature.weather.info

interface ModelMapper<I, O> {
    fun I.mapTo(): O
}