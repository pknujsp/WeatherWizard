package io.github.pknujsp.weatherwizard.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManager
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarter
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val globalRepositoryCacheManager: GlobalRepositoryCacheManager,
    val notificationStarter: NotificationStarter,
) : ViewModel() {

    fun startCacheCleaner() {
        globalRepositoryCacheManager.startCacheCleaner()
    }

    fun stopCacheCleaner() {
        globalRepositoryCacheManager.stopCacheCleaner()
    }

}