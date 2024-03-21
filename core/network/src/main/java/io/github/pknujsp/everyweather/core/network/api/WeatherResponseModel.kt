package io.github.pknujsp.everyweather.core.network.api

import io.github.pknujsp.everyweather.core.model.ApiResponseModel

interface CurrentWeatherResponseModel : ApiResponseModel

interface HourlyForecastResponseModel : ApiResponseModel

interface DailyForecastResponseModel : ApiResponseModel

interface YesterdayWeatherResponseModel : ApiResponseModel

interface AirQualityResponseModel : ApiResponseModel
