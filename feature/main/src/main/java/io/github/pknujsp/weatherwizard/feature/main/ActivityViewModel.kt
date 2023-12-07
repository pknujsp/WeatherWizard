package io.github.pknujsp.weatherwizard.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repositoryInitializerManager: RepositoryInitializerManager
) : ViewModel() {

    private var job: Job? = null


    fun initializeRepositories() {
        if (job?.isActive == true) {
            return
        }

        job = viewModelScope.launch(Dispatchers.Default) {
            repositoryInitializerManager.initialize()
        }
    }

    fun stopRepositoryInitialization() {
        job?.cancel()
    }
}