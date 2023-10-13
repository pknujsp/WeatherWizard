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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetAreaRepository: TargetAreaRepository
) : ViewModel() {

    private val _targetArea = MutableStateFlow<LocationType>(LocationType.CurrentLocation)
    val targetArea: StateFlow<LocationType> = _targetArea

    private val _favoriteAreaList = MutableStateFlow<UiState<List<FavoriteArea>>>(UiState.Loading)
    val favoriteAreaList: StateFlow<UiState<List<FavoriteArea>>> = _favoriteAreaList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _targetArea.value = targetAreaRepository.getTargetArea()
            _favoriteAreaList.value = favoriteAreaRepository.getAll().run {
                UiState.Success(
                    map {
                        FavoriteArea(id = it.id, areaName = it.areaName, countryName = it.countryName,
                            placeId = it.placeId
                        )
                    }
                )
            }
        }
    }

    fun updateTargetArea(locationType: LocationType) {
        viewModelScope.launch(Dispatchers.IO) {
            targetAreaRepository.updateTargetArea(locationType)
            val previousTargetArea = targetArea.value

            while (true) {
                delay(50)
                if (targetAreaRepository.getTargetArea() == locationType) break
            }
            _targetArea.value = locationType
        }
    }
}