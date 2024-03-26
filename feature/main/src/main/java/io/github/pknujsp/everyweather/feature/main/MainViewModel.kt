package io.github.pknujsp.everyweather.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.data.onboarding.AppInitializerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appInitRepository: AppInitializerRepository,
) : ViewModel() {
    val initialized = appInitRepository.initialized
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)
}