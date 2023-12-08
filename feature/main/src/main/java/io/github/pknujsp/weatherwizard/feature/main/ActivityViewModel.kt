package io.github.pknujsp.weatherwizard.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val globalRepositoryCacheManager: GlobalRepositoryCacheManager
) : ViewModel() {

    fun startCacheCleaner() {
        globalRepositoryCacheManager.startCacheCleaner()
    }

    fun stopCacheCleaner() {
        globalRepositoryCacheManager.stopCacheCleaner()
    }

}