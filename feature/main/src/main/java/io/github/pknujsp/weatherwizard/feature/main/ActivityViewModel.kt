package io.github.pknujsp.weatherwizard.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repositoryInitializer: RepositoryInitializer
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.Default) {
            repositoryInitializer.initialize()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}