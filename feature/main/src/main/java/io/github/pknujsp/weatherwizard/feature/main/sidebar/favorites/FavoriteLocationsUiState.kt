package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import kotlinx.coroutines.flow.StateFlow


@Stable
interface FavoriteLocationsUiState {
    val containMore: Boolean
    val favoriteAreas: StateFlow<List<FavoriteArea>>
}