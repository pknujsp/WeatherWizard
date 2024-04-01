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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val DELAY = 16L

@HiltViewModel
class SummaryTextViewModel @Inject constructor(
    private val summaryTextRepository: SummaryTextRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var job: Job? = null
    private val mutableUiState = MutableSummaryUiState()
    val uiState: SummaryUiState = mutableUiState

    private lateinit var model: WeatherSummaryPrompt.Model

    fun summarize(model: WeatherSummaryPrompt.Model) {
        this.model = model
        generate()
    }

    private fun generate() {
        job?.cancel()
        job = viewModelScope.launch {
            withContext(dispatcher) {
                summaryTextRepository.generateContentStream(WeatherSummaryPrompt(model))
            }.onStart {
                mutableUiState.run {
                    isStopped = false
                    isSummarizing = true
                    isWaitingFirstResponse = true
                    error = null
                    summaryText = ""
                    buttonText = io.github.pknujsp.everyweather.core.resource.R.string.stop_summary
                }
            }.onCompletion {
                mutableUiState.run {
                    if (uiState.summaryText.isEmpty()) {
                        isStopped = true
                        buttonText = io.github.pknujsp.everyweather.core.resource.R.string.stopped_summary
                    }
                    isSummarizing = false
                    isWaitingFirstResponse = false
                }
            }.onEmpty {
                mutableUiState.error = io.github.pknujsp.everyweather.core.resource.R.string.error_summary
            }.collect { response ->
                if (response.text == null) {
                    cancel()
                }

                mutableUiState.run {
                    if (isWaitingFirstResponse) {
                        isWaitingFirstResponse = false
                    }
                    for (char in response.text!!) {
                        summaryText += char
                        delay(DELAY)
                    }
                }
            }
        }
    }

    fun stopOrResume() {
        viewModelScope.launch {
            if (job?.isActive == false && uiState.isStopped) {
                generate()
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