package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetAreaRepository: TargetAreaRepository
) : ViewModel() {

    private val _targetLocation = MutableStateFlow<LocationType>(LocationType.CurrentLocation)
    val targetLocation: StateFlow<LocationType> = _targetLocation

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
            _targetLocation.value = targetAreaRepository.getTargetArea()
        }
    }

    fun updateTargetArea(locationType: LocationType) {
        viewModelScope.launch(Dispatchers.IO) {
            targetAreaRepository.updateTargetArea(locationType)
            while (true) {
                delay(50)
                if (targetAreaRepository.getTargetArea() == locationType) break
            }
            _onChanged.value = true
        }
    }
}