package io.github.pknujsp.weatherwizard.core.network.datasource

import io.github.pknujsp.weatherwizard.core.network.ApiResponseModel


interface CurrentWeatherResponseModel : ApiResponseModel

interface HourlyForecastResponseModel : ApiResponseModel

interface DailyForecastResponseModel : ApiResponseModel

interface YesterdayWeatherResponseModel : ApiResponseModel