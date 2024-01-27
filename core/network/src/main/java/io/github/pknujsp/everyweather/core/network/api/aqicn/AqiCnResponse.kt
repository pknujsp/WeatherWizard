package io.github.pknujsp.everyweather.core.network.api.aqicn

import io.github.pknujsp.everyweather.core.network.api.AirQualityResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AqiCnResponse(
    @SerialName("status") val status: String,
    @SerialName("data") val data: Data
) : AirQualityResponseModel {

    @Serializable
    data class Data(
        @SerialName("aqi")
        val aqi: String,
        @SerialName("idx")
        val idx: String,
        @SerialName("attributions")
        val attributions: List<Attribution>,
        @SerialName("dominentpol")
        val dominentPol: String,
        @SerialName("city")
        val city: City,
        @SerialName("iaqi")
        val iaqi: IAqi,
        @SerialName("time")
        val time: Time,
        @SerialName("forecast")
        val forecast: Forecast,
    ) {

        @Serializable
        data class Attribution(
            @SerialName("url")
            val url: String,
            @SerialName("name")
            val name: String
        )

        @Serializable
        data class City(
            @SerialName("geo")
            val geo: List<String>,
            @SerialName("name")
            val name: String,
            @SerialName("url")
            val url: String
        )

        @Serializable
        data class IAqi(
            @SerialName("co")
            val co: Pollutant,
            @SerialName("dew")
            val dew: Pollutant,
            @SerialName("no2")
            val no2: Pollutant,
            @SerialName("o3")
            val o3: Pollutant,
            @SerialName("pm10")
            val pm10: Pollutant,
            @SerialName("pm25")
            val pm25: Pollutant,
            @SerialName("so2")
            val so2: Pollutant,
        ) {

            @Serializable
            data class Pollutant(
                @SerialName("v")
                val v: String
            )
        }

        @Serializable
        data class Time(
            @SerialName("s")
            val s: String,
            @SerialName("tz")
            val tz: String,
            @SerialName("v")
            val v: String,
            @SerialName("iso")
            val iso: String
        )

        @Serializable
        data class Forecast(
            @SerialName("daily")
            val daily: Daily
        ) {

            @Serializable
            data class Daily(
                @SerialName("o3")
                val o3: List<ForecastPollutant>,
                @SerialName("pm10")
                val pm10: List<ForecastPollutant>,
                @SerialName("pm25")
                val pm25: List<ForecastPollutant>,
            ) {

                @Serializable
                data class ForecastPollutant(
                    @SerialName("avg")
                    val avg: String,
                    @SerialName("day")
                    val day: String,
                    @SerialName("max")
                    val max: String,
                    @SerialName("min")
                    val min: String
                )
            }
        }

    }
}