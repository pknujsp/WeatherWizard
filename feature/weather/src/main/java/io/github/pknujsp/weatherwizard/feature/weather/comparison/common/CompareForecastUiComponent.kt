package io.github.pknujsp.weatherwizard.feature.weather.comparison.common

import android.text.util.Linkify
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.jeziellago.compose.markdowntext.MarkdownText
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes


@Composable
internal fun CommonForecastItemsScreen(model: Map<WeatherConditionCategory, String>) {
    Column {
        TitleTextWithoutNavigation(title = stringResource(id = R.string.title_comparison_report))
        Row(verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.horizontalScroll(rememberScrollState())) {
            model.forEach { entry ->
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val aStyle = listOf(AStyle(
                        contentId = listOf("icon" to InlineTextContent(Placeholder(width = 32.sp,
                            height = 28.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center)) {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(entry.key.dayWeatherIcon).crossfade(false)
                                .build(), contentDescription = null)
                        }),
                    ),
                        AStyle(
                            text = stringResource(id = entry.key.stringRes),
                        ))

                    Text(text = aStyle.toAnnotated(),
                        style = TextStyle(fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold),
                        inlineContent = aStyle.first().inlineContents)

                    MarkdownText(
                        markdown = entry.value, style = TextStyle(fontSize = 15.sp, lineHeight = 3.sp),
                        disableLinkMovementMethod = true, linkifyMask = 0,
                    )
                }
            }
        }
    }
}


internal object CompareForecastCard {
    private val surfaceModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp)
    private val backgroundColor = Color(107, 107, 107, 217)

    @Composable
    fun CompareCardSurface(content: @Composable () -> Unit) {
        Surface(
            modifier = surfaceModifier,
            shape = AppShapes.large,
            color = backgroundColor,
        ) {
            content()
        }
    }
}