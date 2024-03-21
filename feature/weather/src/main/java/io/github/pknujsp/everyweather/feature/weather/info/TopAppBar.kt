package io.github.pknujsp.everyweather.feature.weather.info

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.appbar.CustomTopAppBar
import io.github.pknujsp.everyweather.core.ui.appbar.CustomTopAppBarColors
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.everyweather.core.ui.theme.outlineTextStyle
import io.github.pknujsp.everyweather.core.ui.theme.shadowBox
import kotlinx.serialization.json.JsonNull.content

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    topAppBarUiState: TopAppBarUiState,
    weatherContentUiState: WeatherContentUiState.Success,
    openDrawer: () -> Unit,
    reload: () -> Unit,
    summarize: () -> Unit,
    onClickedWeatherProviderButton: () -> Unit,
) {
    CustomTopAppBar(
        smallTitle = {
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp),
                    )
                    Text(
                        text = topAppBarUiState.address ?: stringResource(id = R.string.unknown_address),
                        color = Color.White,
                        fontSize = 14.sp,
                        style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                    )
                }
                Text(
                    text = weatherContentUiState.dateTime,
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                    color = Color.White,
                    style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                )
            }
        },
        bigTitle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 62.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = listOf(
                        AStyle(
                            "${topAppBarUiState.address ?: stringResource(id = R.string.unknown_address)}\n",
                            span = SpanStyle(fontSize = 24.sp),
                        ),
                        AStyle(
                            topAppBarUiState.country ?: stringResource(id = R.string.unknown_country),
                            span = SpanStyle(
                                fontSize = 15.sp,
                            ),
                        ),
                    ).toAnnotated(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 26.sp,
                    style = LocalTextStyle.current.merge(outlineTextStyle),
                )
                InfoItem(image = weatherContentUiState.args.weatherProvider.icon,
                    content = stringResource(id = weatherContentUiState.args.weatherProvider.title),
                    onClick = { onClickedWeatherProviderButton() })
                InfoItem(image = null, content = weatherContentUiState.dateTime, onClick = null)
            }
        },
        actions = {
            IconButton(modifier = modifier, onClick = summarize) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shine_128_128),
                    modifier = Modifier.size(24.dp),
                    contentDescription = null,
                )
            }
            IconButton(modifier = modifier, onClick = reload) {
                Icon(painter = painterResource(id = R.drawable.ic_refresh), modifier = Modifier.size(24.dp), contentDescription = null)
            }
        },
        scrollState = scrollState,
        colors = defaultCustomTopAppBarColors,
        modifier = modifier
            .background(brush = shadowBox())
            .statusBarsPadding(),
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationIcon = {
            IconButton(modifier = modifier, onClick = {
                openDrawer()
            }) {
                Icon(Icons.Rounded.Menu, contentDescription = null)
            }
        },
    )
}

@Composable
private fun InfoItem(@DrawableRes image: Int?, content: String, onClick: (() -> Unit)?) {
    Box(modifier = Modifier
        .background(color = Color.Gray.copy(alpha = 0.4f), AppShapes.medium)
        .clip(AppShapes.medium)
        .clickable(enabled = onClick != null) {
            onClick?.invoke()
        }, contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            image?.let {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(it).crossfade(false).build(),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = content,
                fontSize = 13.sp,
                color = Color.White,
            )
        }
    }
}

@Stable
interface TopAppBarUiState {
    val address: String?
    val country: String?
}

private val defaultCustomTopAppBarColors = CustomTopAppBarColors()