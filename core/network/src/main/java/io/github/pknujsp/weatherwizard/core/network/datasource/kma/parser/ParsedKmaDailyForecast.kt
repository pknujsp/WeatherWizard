package io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser


data class ParsedKmaDailyForecast(
    val date: String = "",
    val isSingle: Boolean = false,
    val amValues: Values? = null,
    val pmValues: Values? = null,
    val singleValues: Values? = null,
    val minTemp: Double,
    val maxTemp: Double,
) {
    data class Values(
        var weatherDescription: String = "",
        var pop: Int,
    )

}