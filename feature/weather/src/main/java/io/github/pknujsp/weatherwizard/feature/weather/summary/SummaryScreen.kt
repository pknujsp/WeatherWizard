package io.github.pknujsp.weatherwizard.feature.weather.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.AlwaysOnBottomSheetDialog


@Composable
fun SummaryScreen(model: WeatherDataParser.Model, onDismiss: () -> Unit, summaryTextViewModel: SummaryTextViewModel = hiltViewModel()) {
    val uiState = summaryTextViewModel.uiState
    LaunchedEffect(model) {
        summaryTextViewModel.summarize(model)
    }
    AlwaysOnBottomSheetDialog(title = stringResource(id = R.string.title_ai_summary), onDismiss = onDismiss) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            MarkdownText(
                modifier = Modifier.padding(8.dp),
                markdown = uiState.summaryText,
            )
            if (uiState.isSummarizing) {
                SummarizingCard()
            }
            Footer()
        }
    }
}

@Composable
private fun BoxScope.SummarizingCard(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.align(Alignment.BottomCenter),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.Black)
            Text(text = stringResource(id = R.string.text_summarizing), style = TextStyle(color = Color.Black, fontSize = 13.sp))
        }
    }
}

@Composable
private fun BoxScope.Footer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.align(Alignment.BottomEnd),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        Text(text = "with", style = TextStyle(color = Color.Gray, fontSize = 12.sp))
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(R.drawable.gemini_300_150).crossfade(false).build(),
            modifier = modifier.height(23.dp),
            contentDescription = null)
    }
}