package io.github.pknujsp.everyweather.feature.weather.main


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.FailedReason
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.common.util.toTimeZone
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.domain.location.CurrentLocationState
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationCoordinate
import io.github.pknujsp.everyweather.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.WeatherResponseState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.everyweather.core.ui.weather.item.DynamicDateTimeUiCreator
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.DetailDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TargetLocationModel
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeatherMainViewModel @Inject constructor(
    targetLocationRepository: TargetLocationRepository,
) : ViewModel() {
    val selectedLocation: StateFlow<SelectedLocationModel?> =
        targetLocationRepository.targetLocation.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}