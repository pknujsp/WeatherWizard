package io.github.pknujsp.everyweather.core.network.api.metnorway

import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetNorwayResponse(
    @SerialName("geometry") val geometry: Geometry,
    @SerialName("properties") val properties: Properties,
    @SerialName("type") val type: String = "", // Feature
) : ApiResponseModel {
    /**
     * @param coordinates [longitude, latitude, ?]
     * @param type Point
     */
    @Serializable
    data class Geometry(
        @SerialName("coordinates") val coordinates: List<String>,
        @SerialName("type") val type: String = "", // Point
    )

    @Serializable
    data class Properties(
        @SerialName("meta") val meta: Meta,
        @SerialName("timeseries") val timeseries: List<Timesery>,
    ) {
        @Serializable
        data class Meta(
            @SerialName("units") val units: Units,
            @SerialName("updated_at") val updatedAt: String = "", // 2023-09-24T13:30:33Z
        ) {
            /**
             * @param airPressureAtSeaLevel hPa
             * @param airTemperature celsius
             * @param airTemperatureMax celsius
             * @param airTemperatureMin celsius
             * @param cloudAreaFraction %
             * @param cloudAreaFractionHigh %
             * @param cloudAreaFractionLow %
             * @param cloudAreaFractionMedium %
             * @param dewPointTemperature celsius
             * @param fogAreaFraction %
             * @param precipitationAmount mm
             * @param relativeHumidity %
             * @param ultravioletIndexClearSky 1
             * @param windFromDirection degrees
             * @param windSpeed m/s
             */
            @Serializable
            data class Units(
                @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: String = "", // hPa
                @SerialName("air_temperature") val airTemperature: String = "", // celsius
                @SerialName("air_temperature_max") val airTemperatureMax: String = "", // celsius
                @SerialName("air_temperature_min") val airTemperatureMin: String = "", // celsius
                @SerialName("cloud_area_fraction") val cloudAreaFraction: String = "", // %
                @SerialName("cloud_area_fraction_high") val cloudAreaFractionHigh: String = "", // %
                @SerialName("cloud_area_fraction_low") val cloudAreaFractionLow: String = "", // %
                @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMedium: String = "", // %
                @SerialName("dew_point_temperature") val dewPointTemperature: String = "", // celsius
                @SerialName("fog_area_fraction") val fogAreaFraction: String = "", // %
                @SerialName("precipitation_amount") val precipitationAmount: String = "", // mm
                @SerialName("relative_humidity") val relativeHumidity: String = "", // %
                @SerialName("ultraviolet_index_clear_sky") val ultravioletIndexClearSky: String = "", // 1
                @SerialName("wind_from_direction") val windFromDirection: String = "", // degrees
                @SerialName("wind_speed") val windSpeed: String = "", // m/s
            )
        }

        /**
         * @param data
         * @param time 2023-09-24T15:00:00Z
         */
        @Serializable
        data class Timesery(
            @SerialName("data") val data: Data,
            @SerialName("time") val time: String = "", // 2023-09-24T15:00:00Z
        ) {
            @Serializable
            data class Data(
                @SerialName("instant") val instant: Instant,
                @SerialName("next_12_hours") val next12Hours: Next12Hours? = null,
                @SerialName("next_1_hours") val next1Hours: Next1Hours? = null,
                @SerialName("next_6_hours") val next6Hours: Next6Hours? = null,
            ) {
                @Serializable
                data class Instant(
                    @SerialName("details") val details: Details,
                ) {
                    /**
                     * @param airPressureAtSeaLevel 1018.1hPa
                     * @param airTemperature 16.0celsius
                     * @param cloudAreaFraction 38.3%
                     * @param cloudAreaFractionHigh 35.2%
                     * @param cloudAreaFractionLow 1.6%
                     * @param cloudAreaFractionMedium 9.4%
                     * @param dewPointTemperature 14.1celsius
                     * @param fogAreaFraction 0.0%
                     * @param relativeHumidity 87.4%
                     * @param ultravioletIndexClearSky 0.0
                     * @param windFromDirection 29.9degrees
                     * @param windSpeed 2.3m/s
                     */
                    @Serializable
                    data class Details(
                        @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: Double = 0.0, // 1018.1
                        @SerialName("air_temperature") val airTemperature: Double = 0.0, // 16.0
                        @SerialName("cloud_area_fraction") val cloudAreaFraction: Double = 0.0, // 38.3
                        @SerialName("cloud_area_fraction_high") val cloudAreaFractionHigh: Double = 0.0, // 35.2
                        @SerialName("cloud_area_fraction_low") val cloudAreaFractionLow: Double = 0.0, // 1.6
                        @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMedium: Double = 0.0, // 9.4
                        @SerialName("dew_point_temperature") val dewPointTemperature: Double = 0.0, // 14.1
                        @SerialName("fog_area_fraction") val fogAreaFraction: Double = 0.0, // 0.0
                        @SerialName("relative_humidity") val relativeHumidity: Double = 0.0, // 87.4
                        @SerialName("ultraviolet_index_clear_sky") val ultravioletIndexClearSky: Double = 0.0, // 0.0
                        @SerialName("wind_from_direction") val windFromDirection: Double = 0.0, // 29.9
                        @SerialName("wind_speed") val windSpeed: Double = 0.0, // 2.3
                    )
                }

                @Serializable
                data class Next12Hours(
                    @SerialName("summary") val summary: Summary,
                ) {
                    /**
                     * @param symbolCode partlycloudy_day
                     */
                    @Serializable
                    data class Summary(
                        @SerialName("symbol_code") val symbolCode: String = "", // partlycloudy_day
                    )
                }

                @Serializable
                data class Next1Hours(
                    @SerialName("details") val details: Details,
                    @SerialName("summary") val summary: Summary,
                ) {
                    /**
                     * @param precipitationAmount 0.0
                     */
                    @Serializable
                    data class Details(
                        @SerialName("precipitation_amount") val precipitationAmount: Double = 0.0, // 0.0
                    )

                    /**
                     * @param symbolCode partlycloudy_night
                     */
                    @Serializable
                    data class Summary(
                        @SerialName("symbol_code") val symbolCode: String = "", // partlycloudy_night
                    )
                }

                @Serializable
                data class Next6Hours(
                    @SerialName("details") val details: Details,
                    @SerialName("summary") val summary: Summary,
                ) {
                    /**
                     * @param airTemperatureMax 16.4celsius
                     * @param airTemperatureMin 15.4celsius
                     * @param precipitationAmount 0.0
                     */
                    @Serializable
                    data class Details(
                        @SerialName("air_temperature_max") val airTemperatureMax: Double = 0.0, // 16.4
                        @SerialName("air_temperature_min") val airTemperatureMin: Double = 0.0, // 15.4
                        @SerialName("precipitation_amount") val precipitationAmount: Double = 0.0, // 0.0
                    )

                    /**
                     * @param symbolCode partlycloudy_night
                     */
                    @Serializable
                    data class Summary(
                        @SerialName("symbol_code") val symbolCode: String = "", // partlycloudy_night
                    )
                }
            }
        }
    }
}
