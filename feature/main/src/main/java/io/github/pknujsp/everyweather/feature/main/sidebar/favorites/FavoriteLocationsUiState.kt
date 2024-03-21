package io.github.pknujsp.everyweather.feature.main.sidebar.favorites

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.favorite.FavoriteArea
import kotlinx.coroutines.flow.StateFlow

@Stable
interface FavoriteLocationsUiState {
    val favoriteAreas: StateFlow<List<FavoriteArea>>
}
