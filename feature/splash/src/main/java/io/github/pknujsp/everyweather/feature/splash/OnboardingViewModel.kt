package io.github.pknujsp.everyweather.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.data.onboarding.AppInitializerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: AppInitializerRepository
) : ViewModel() {
    fun initialize() {
        viewModelScope.launch {
            settingsRepository.initialize()
        }
    }
}