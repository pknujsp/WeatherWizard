package io.github.pknujsp.everyweather.feature.flickr

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.flickr.FlickrRepository
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.everyweather.core.resource.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FlickrImageViewModel
    @Inject
    constructor(
        private val flickrRepository: FlickrRepository,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : ViewModel() {
        private var requestParameter: FlickrRequestParameters? = null

        private var job: Job? = null

        var flickrImageUiState by mutableStateOf(FlickrImageUiState())
            private set

        fun initialize(requestParameter: FlickrRequestParameters) {
            viewModelScope.launch {
                this@FlickrImageViewModel.requestParameter = requestParameter
                load()
            }
        }

        fun load() {
            job?.cancel()
            requestParameter?.let {
                job =
                    viewModelScope.launch {
                        // flickrImageUiState = flickrImageUiState.copy(isLoaded = false, isLoading = true, textRes = R.string.loading_image)

                        withContext(ioDispatcher) { flickrRepository.getPhoto(it) }.onSuccess {
                            flickrImageUiState = flickrImageUiState.copy(url = it.imageUrl, isLoaded = true, isLoading = false)
                        }.onFailure {
                            flickrImageUiState =
                                flickrImageUiState.copy(isLoaded = false, isLoading = false, textRes = R.string.retry_to_load_image_if_failed)
                        }
                    }
            }
        }
    }
