package io.github.pknujsp.weatherwizard.core.network.api

import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel


interface CurrentWeatherResponseModel : ApiResponseModel

interface HourlyForecastResponseModel : ApiResponseModel

interface DailyForecastResponseModel : ApiResponseModel

interface YesterdayWeatherResponseModel : ApiResponseModel

interface AirQualityResponseModel : ApiResponseModel