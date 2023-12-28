package io.github.pknujsp.weatherwizard.core.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    var imageUrl: String? by mutableStateOf(null)
        private set

    fun updateImageUrl(url: String?) {
        viewModelScope.launch {
            imageUrl = url
        }
    }
}