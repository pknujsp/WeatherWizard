package io.github.pknujsp.weatherwizard.feature.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.GlobalRepositoryCacheManager
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarter
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