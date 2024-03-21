package io.github.pknujsp.everyweather.feature.weather.summary

import android.text.util.Linkify
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.ModalBottomSheetDialog
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes

@Composable
fun SummaryScreen(
    model: WeatherSummaryPrompt.Model,
    summaryTextViewModel: SummaryTextViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val uiState = summaryTextViewModel.uiState
    LaunchedEffect(model) {
        summaryTextViewModel.summarize(model)
    }

    ModalBottomSheetDialog(title = stringResource(id = R.string.title_ai_summary), onDismiss = onDismiss) {
        val scrollState = rememberScrollState()

        LaunchedEffect(uiState.summaryText) {
            scrollState.scrollTo(scrollState.maxValue)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, true),
            ) {
                MarkdownText(
                    style = TextStyle(color = Color.Black, fontSize = 15.sp, lineHeight = 4.sp),
                    markdown = if (uiState.error != null) stringResource(id = uiState.error!!) else uiState.summaryText,
                    linkifyMask = Linkify.WEB_URLS,
                )
            }
            TextPlaceHolder {
                uiState.isWaitingFirstResponse
            }
            if (uiState.isSummarizing || uiState.isStopped) {
                SummarizingCard(uiState = uiState, stop = {
                    summaryTextViewModel.stopOrResume()
                })
            }
        }
    }
}

@Composable
private fun BoxScope.SummarizingCard(
    modifier: Modifier = Modifier,
    uiState: SummaryUiState,
    stop: () -> Unit,
) {
    val currentStopOrResume by rememberUpdatedState(newValue = stop)
    Box(
        modifier =
            modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter),
    ) {
        OutlinedButton(
            onClick = currentStopOrResume,
            shape = AppShapes.extraLarge,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (uiState.isSummarizing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
                }
                Text(text = stringResource(id = uiState.buttonText), style = TextStyle(color = Color.Black, fontSize = 13.sp))
            }
        }
    }
}