package io.github.pknujsp.weatherwizard.feature.flickr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.flickr.FlickrRepository
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlickrImageViewModel @Inject constructor(
    private val flickrRepository: FlickrRepository
) : ViewModel() {

    private var requestParameter: FlickrRequestParameters? = null

    var flickrImageUiState by mutableStateOf(FlickrImageUiState())
        private set

    fun initialize(
        requestParameter: FlickrRequestParameters
    ) {
        this.requestParameter = requestParameter
        load()
    }

    fun load() {
        if (requestParameter != null) {
            viewModelScope.launch {
                //flickrImageUiState = flickrImageUiState.copy(isLoaded = false, isLoading = true, textRes = R.string.loading_image)

                flickrRepository.getPhoto(requestParameter!!).onSuccess {
                    flickrImageUiState = flickrImageUiState.copy(url = it.imageUrl, isLoaded = true, isLoading = false)
                }.onFailure {
                    flickrImageUiState =
                        flickrImageUiState.copy(isLoaded = false, isLoading = false, textRes = R.string.retry_to_load_image_if_failed)
                }
            }
        }
    }


}