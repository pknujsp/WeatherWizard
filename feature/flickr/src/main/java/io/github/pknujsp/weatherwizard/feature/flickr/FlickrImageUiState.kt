package io.github.pknujsp.weatherwizard.feature.flickr

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrImageEntity

@Stable
data class FlickrImageUiState(
    val flickerImageEntity: FlickrImageEntity? = null,
    val url: String = "",
    val isLoaded: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes val textRes: Int = R.string.loading_image
)