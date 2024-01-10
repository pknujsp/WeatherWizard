package io.github.pknujsp.weatherwizard.feature.main.sidebar.favorites

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import kotlinx.coroutines.flow.StateFlow


@Stable
interface FavoriteLocationsUiState {
    val favoriteAreas: StateFlow<List<FavoriteArea>>
}