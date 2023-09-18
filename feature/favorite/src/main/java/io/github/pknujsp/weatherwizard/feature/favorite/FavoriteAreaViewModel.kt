package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor(
    private val favoriteAreaRepository: FavoriteAreaListRepository,
    private val targetAreaRepository: TargetAreaRepository
) : ViewModel() {

    private val _targetArea = MutableStateFlow<TargetAreaType>(TargetAreaType.CurrentLocation)
    val targetArea: StateFlow<TargetAreaType> = _targetArea

    private val _favoriteAreaList = MutableStateFlow<UiState<List<FavoriteArea>>>(UiState.Loading)
    val favoriteAreaList: StateFlow<UiState<List<FavoriteArea>>> = _favoriteAreaList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _targetArea.value = targetAreaRepository.getTargetArea()
            _favoriteAreaList.value = favoriteAreaRepository.getAll().run {
                UiState.Success(
                    map {
                        FavoriteArea(id = it.id, areaName = it.areaName, countryName = it.countryName
                        )
                    }
                )
            }
        }
    }

    fun updateTargetArea(targetAreaType: TargetAreaType) {
        viewModelScope.launch(Dispatchers.IO) {
            targetAreaRepository.updateTargetArea(targetAreaType)
            _targetArea.value = targetAreaType
        }
    }
}