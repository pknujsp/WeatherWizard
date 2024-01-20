package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.ai.SummaryTextRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SummaryTextViewModel @Inject constructor(
    private val summaryTextRepository: SummaryTextRepository,
    @CoDispatcher(CoDispatcherType.SINGLE) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var job: Job? = null
    private val mutableUiState = MutableSummaryUiState()
    val uiState: SummaryUiState = mutableUiState

    private var model: WeatherSummaryPrompt.Model? = null

    fun summarize(model: WeatherSummaryPrompt.Model) {
        this.model = model
        generate(model)
    }

    fun regenerate() {
        if (model != null) {
            generate(model!!)
        }
    }

    private fun generate(model: WeatherSummaryPrompt.Model) {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(dispatcher) {
                summaryTextRepository.generateContentStream(WeatherSummaryPrompt(model))
            }.onStart {
                mutableUiState.isStopped = false
                mutableUiState.isSummarizing = true
                mutableUiState.summaryText = ""
                mutableUiState.buttonText = io.github.pknujsp.weatherwizard.core.resource.R.string.stop_summary
            }.onCompletion {
                mutableUiState.isSummarizing = false
            }.collect {
                mutableUiState.summaryText = mutableUiState.summaryText + it.text
            }
        }
    }

    fun stop() {
        viewModelScope.launch {
            if (job?.isActive == false) {
                return@launch
            }

            job?.cancel()
            mutableUiState.isStopped = true
            mutableUiState.buttonText = io.github.pknujsp.weatherwizard.core.resource.R.string.stopped_summary
        }
    }
}

private class MutableSummaryUiState : SummaryUiState {
    override var isSummarizing: Boolean by mutableStateOf(true)
    override var isStopped: Boolean by mutableStateOf(false)
    override var summaryText: String by mutableStateOf("")
    override var error: String? by mutableStateOf(null)
    override var buttonText: Int by mutableIntStateOf(io.github.pknujsp.weatherwizard.core.resource.R.string.stop_summary)
}