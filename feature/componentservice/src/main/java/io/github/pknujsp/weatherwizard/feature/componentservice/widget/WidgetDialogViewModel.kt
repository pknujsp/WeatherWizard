package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import javax.inject.Inject

@HiltViewModel
class WidgetDialogViewModel @Inject constructor(
    val widgetManager: WidgetManager,
) : ViewModel() {}