package io.github.pknujsp.weatherwizard.feature.flickr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.flickr.FlickrRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrImageEntity
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlickrImageViewModel @Inject constructor(
    private val flickrRepository: FlickrRepository
) : ViewModel() {

    private val _image = MutableStateFlow<UiState<FlickrImageEntity>>(UiState.Loading)
    val image: StateFlow<UiState<FlickrImageEntity>> = _image

    private val parameter = MutableStateFlow<FlickrRequestParameters?>(null)

    fun load(
        requestParameter: FlickrRequestParameters
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            parameter.value = requestParameter
            flickrRepository.getPhoto(requestParameter).onSuccess {
                _image.value = UiState.Success(it)
            }.onFailure {
                _image.value = UiState.Error(it)
            }
        }
    }

    fun reload() {
        parameter.value?.run {
            load(this)
        }
    }



}