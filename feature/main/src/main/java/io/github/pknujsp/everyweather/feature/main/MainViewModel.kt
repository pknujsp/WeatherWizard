package io.github.pknujsp.everyweather.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.everyweather.core.data.onboarding.AppInitializerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val DELAY = 20L

@HiltViewModel
class MainViewModel @Inject constructor(
    appInitRepository: AppInitializerRepository,
    targetLocationRepository: TargetLocationRepository,
    @CoDispatcher(CoDispatcherType.DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    val initialized = appInitRepository.initialized.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isTargetLocationChanged = flow {
        var lastTargetLocation: SelectedLocationModel? = null

        targetLocationRepository.targetLocation.filter {
            lastTargetLocation != it
        }.collect {
            if (lastTargetLocation != null) {
                emit(true)
                withContext(defaultDispatcher) {
                    delay(DELAY)
                }
                emit(false)
            }
            lastTargetLocation = it
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

}