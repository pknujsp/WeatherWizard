package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteAreaViewModel @Inject constructor() : ViewModel() {


    init {
        viewModelScope.launch(Dispatchers.IO) { }
    }
}