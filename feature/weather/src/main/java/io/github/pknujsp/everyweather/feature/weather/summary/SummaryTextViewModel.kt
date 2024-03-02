package io.github.pknujsp.everyweather.feature.weather.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.ai.SummaryTextRepository
import io.github.pknujsp.everyweather.feature.weather.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
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

    private fun regenerate() {
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
                mutableUiState.isWaitingFirstResponse = true
                mutableUiState.error = null
                mutableUiState.summaryText = ""
                mutableUiState.buttonText = io.github.pknujsp.everyweather.core.resource.R.string.stop_summary
            }.onCompletion {
                if (uiState.summaryText.isEmpty()) {
                    mutableUiState.isStopped = true
                    mutableUiState.buttonText = io.github.pknujsp.everyweather.core.resource.R.string.stopped_summary
                }
                mutableUiState.isSummarizing = false
                mutableUiState.isWaitingFirstResponse = false
            }.onEmpty {
                mutableUiState.error = io.github.pknujsp.everyweather.core.resource.R.string.error_summary
            }.collect {
                if (uiState.isWaitingFirstResponse) {
                    mutableUiState.isWaitingFirstResponse = false
                }
                mutableUiState.summaryText += it.text
            }
        }
    }

    fun stopOrResume() {
        viewModelScope.launch {
            if (job?.isActive == false && uiState.isStopped) {
                regenerate()
                return@launch
            }

            job?.cancel()
            mutableUiState.isStopped = true
            mutableUiState.buttonText = io.github.pknujsp.everyweather.core.resource.R.string.stopped_summary
        }
    }
}

private class MutableSummaryUiState : SummaryUiState {
    override var isSummarizing: Boolean by mutableStateOf(true)
    override var isWaitingFirstResponse: Boolean by mutableStateOf(false)
    override var isStopped: Boolean by mutableStateOf(false)
    override var summaryText: String by mutableStateOf("")
    override var error: Int? by mutableStateOf(null)
    override var buttonText: Int by mutableIntStateOf(io.github.pknujsp.everyweather.core.resource.R.string.stop_summary)
}