package io.github.pknujsp.weatherwizard.core.network.api.metnorway

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaCurrentWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaDailyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaHourlyForecastResponse
import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaYesterdayWeatherResponse
import io.github.pknujsp.weatherwizard.core.network.retrofit.onResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import java.time.ZonedDateTime
import javax.inject.Inject

class MetNorwayDataSourceImpl @Inject constructor(
    private val metNorwayNetworkApi: MetNorwayNetworkApi
) : MetNorwayDataSource {


    override suspend fun getLocationForecast(latitude: Double, longitude: Double): Result<MetNorwayResponse> {
        return metNorwayNetworkApi.getLocationForecast(latitude, longitude).onResult()
    }


}