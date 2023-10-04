package io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.feature.weather.comparison.common.CompareForecastViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareHourlyForecastViewModel @Inject constructor(
) : CompareForecastViewModel() {

    override fun load(args: RequestWeatherDataArgs) {
        TODO("Not yet implemented")
    }


}