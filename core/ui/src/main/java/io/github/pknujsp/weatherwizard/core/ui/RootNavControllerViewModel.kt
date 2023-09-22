package io.github.pknujsp.weatherwizard.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootNavControllerViewModel @Inject constructor(
) : ViewModel() {

    private val _requestedRoute = MutableStateFlow<NewRoute>(NewRoute.Initial)
    val requestedRoute: StateFlow<NewRoute> = _requestedRoute

    fun navigate(route: MainRoutes) {
        viewModelScope.launch {
            _requestedRoute.value = NewRoute.Requested(route)
        }
    }
}

sealed interface NewRoute {
    data object Initial : NewRoute

    data class Requested(val route: MainRoutes) : NewRoute
}