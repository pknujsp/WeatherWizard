package io.github.pknujsp.everyweather.core.network.api.kma.parser


data class ParsedKmaDailyForecast(
    val date: String = "",
    val isSingle: Boolean = false,
    val amValues: Values? = null,
    val pmValues: Values? = null,
    val singleValues: Values? = null,
    val minTemp: Short,
    val maxTemp: Short,
) {
    data class Values(
        var weatherDescription: String = "",
        var pop: Int,
    )

}