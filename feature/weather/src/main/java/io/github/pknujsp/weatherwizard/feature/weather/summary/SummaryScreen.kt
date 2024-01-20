package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.AlwaysOnBottomSheetDialog


@Composable
fun SummaryScreen(model: WeatherSummaryPrompt.Model, onDismiss: () -> Unit, summaryTextViewModel: SummaryTextViewModel = hiltViewModel()) {
    val uiState = summaryTextViewModel.uiState
    LaunchedEffect(model) {
        summaryTextViewModel.summarize(model)
    }
    AlwaysOnBottomSheetDialog(title = stringResource(id = R.string.title_ai_summary), onDismiss = onDismiss) {
        val scrollState = rememberScrollState()
        LaunchedEffect(uiState.summaryText) {
            scrollState.scrollTo(scrollState.maxValue)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState, true)) {
                MarkdownText(
                    modifier = Modifier.padding(bottom = 32.dp),
                    markdown = uiState.summaryText,
                )
            }

            if (uiState.isSummarizing || uiState.isStopped) {
                SummarizingCard(uiState = uiState, stop = {
                    summaryTextViewModel.stop()
                })
            }
            Footer()
        }
    }
}

@Composable
private fun BoxScope.SummarizingCard(modifier: Modifier = Modifier, uiState: SummaryUiState, stop: () -> Unit) {
    val currentStop by rememberUpdatedState(newValue = stop)
    ElevatedCard(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .clickable {
                currentStop()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            if (uiState.isSummarizing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
            }
            Text(text = stringResource(id = uiState.buttonText), style = TextStyle(color = Color.Black, fontSize = 13.sp))
        }
    }
}

@Composable
private fun BoxScope.Footer(modifier: Modifier = Modifier) {
    Row(modifier = modifier.align(Alignment.BottomEnd),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom) {
        Text(text = "with", style = TextStyle(color = Color.Gray, fontSize = 12.sp))
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(R.drawable.gemini_300_150).crossfade(false).build(),
            modifier = modifier.height(24.dp),
            contentDescription = null)
    }
}