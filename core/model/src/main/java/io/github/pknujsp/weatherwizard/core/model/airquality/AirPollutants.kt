package io.github.pknujsp.weatherwizard.core.model.airquality

import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.model.R

enum class AirPollutants(@StringRes val nameResId: Int, @StringRes val descriptionResId: Int, val alias: String) {
    PM10(R.string.air_pollutant_pm10, R.string.air_pollutant_pm10_description, "pm10"),
    PM25(R.string.air_pollutant_pm25, R.string.air_pollutant_pm25_description, "pm25"),
    O3(R.string.air_pollutant_o3, R.string.air_pollutant_o3_description, "o3"),
    NO2(R.string.air_pollutant_no2, R.string.air_pollutant_no2_description, "no2"),
    CO(R.string.air_pollutant_co, R.string.air_pollutant_co_description, "co"),
    SO2(R.string.air_pollutant_so2, R.string.air_pollutant_so2_description, "so2"),
}