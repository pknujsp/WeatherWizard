package io.github.pknujsp.everyweather.feature.flickr

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

@Stable
data class FlickrImageUiState(
    val url: String = "",
    val isLoaded: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes val textRes: Int = io.github.pknujsp.everyweather.core.resource.R.string.loading_image,
)
