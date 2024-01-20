package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.ai.SummaryTextRepository
import kotlinx.coroutines.CoroutineDispatcher
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

    private val mutableUiState = MutableSummaryUiState()
    val uiState: SummaryUiState = mutableUiState

    private var model: WeatherDataParser.Model? = null

    fun summarize(model: WeatherDataParser.Model) {
        this.model = model
        generate(model)
    }

    fun regenerate() {
        if (model != null) {
            generate(model!!)
        }
    }

    private fun generate(model: WeatherDataParser.Model) {
        viewModelScope.launch {
            val prompt = WeatherDataParser.parse(model)

            /* withContext(dispatcher) {
                 summaryTextRepository.generateContentStream(0, prompt)
             }.onStart {
                 mutableUiState.isSummarizing = true
                 mutableUiState.summaryText = ""
             }.onCompletion {
                 mutableUiState.isSummarizing = false
             }.collect {
                 mutableUiState.summaryText = mutableUiState.summaryText + it.text
             }*/
        }
    }

}

private class MutableSummaryUiState : SummaryUiState {
    override var isSummarizing: Boolean by mutableStateOf(true)
    override var summaryText: String by mutableStateOf("")
    override var error: String? by mutableStateOf(null)
}