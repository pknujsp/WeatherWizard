package io.github.pknujsp.everyweather.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.GlobalRepositoryCacheManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel
    @Inject
    constructor(
        private val globalRepositoryCacheManager: GlobalRepositoryCacheManager,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : ViewModel(), GlobalRepositoryCacheManager by globalRepositoryCacheManager {
        override fun startCacheCleaner() {
            viewModelScope.launch(ioDispatcher) {
                globalRepositoryCacheManager.startCacheCleaner()
            }
        }

        override fun stopCacheCleaner() {
            viewModelScope.launch(ioDispatcher) {
                globalRepositoryCacheManager.stopCacheCleaner()
            }
        }
    }
