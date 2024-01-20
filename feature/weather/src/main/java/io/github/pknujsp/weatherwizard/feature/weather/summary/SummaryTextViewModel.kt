package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.lifecycle.ViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.ai.SummaryTextRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SummaryTextViewModel @Inject constructor(
    private val summaryTextRepository: SummaryTextRepository,
    @CoDispatcher(CoDispatcherType.SINGLE) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {


    fun summarize(){

    }


}