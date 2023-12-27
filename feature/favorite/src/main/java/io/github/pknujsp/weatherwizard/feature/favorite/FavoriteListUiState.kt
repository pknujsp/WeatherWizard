package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.feature.favorite.model.TargetLocationUiState


@Stable
interface FavoriteListUiState {
    val favoriteLocations: List<FavoriteArea>?
}