package io.github.pknujsp.weatherwizard.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootNavControllerViewModel @Inject constructor(
) : ViewModel() {

    private val _requestedRoute = MutableSharedFlow<NewRoute>(replay = 0, extraBufferCapacity = 1)
    val requestedRoute: SharedFlow<NewRoute> = _requestedRoute

    fun navigate(route: MainRoutes) {
        viewModelScope.launch {
            _requestedRoute.emit(NewRoute.Requested(route))
        }
    }
}

sealed interface NewRoute {
    data object Initial : NewRoute

    data class Requested(val route: MainRoutes) : NewRoute
}