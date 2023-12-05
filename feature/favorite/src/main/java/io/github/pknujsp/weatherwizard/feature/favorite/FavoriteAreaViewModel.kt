package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    favoriteAreaRepository: FavoriteAreaListRepository, private val targetLocationRepository: TargetLocationRepository
) : ViewModel() {

    private val _targetLocation = MutableStateFlow<SelectedLocationModel?>(null)
    val targetLocation: StateFlow<SelectedLocationModel?> = _targetLocation.asStateFlow()

    val favoriteLocationList = flow {
        emit(favoriteAreaRepository.getAll())
    }.map { entities ->
        entities.map {
            FavoriteArea(it.id, it.placeId, it.areaName, it.countryName)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _onChanged = MutableStateFlow(false)
    val onChanged: StateFlow<Boolean> = _onChanged

    init {
        viewModelScope.launch {
            _targetLocation.value = targetLocationRepository.getTargetLocation()
        }
    }

    fun updateTargetLocation(newModel: SelectedLocationModel) {
        viewModelScope.launch {
            targetLocationRepository.updateTargetLocation(newModel)
            while (true) {
                delay(50)
                if (targetLocationRepository.getTargetLocation() == newModel) break
            }
            _onChanged.value = true
        }
    }
}