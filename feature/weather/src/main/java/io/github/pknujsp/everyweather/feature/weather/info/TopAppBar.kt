package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.everyweather.core.ui.theme.outlineTextStyle
import io.github.pknujsp.everyweather.core.ui.theme.shadowBox
import io.github.pknujsp.everyweather.feature.weather.CustomTopAppBar
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TopAppBarUiState

@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    topAppBarUiState: TopAppBarUiState,
    weatherContentUiState: WeatherContentUiState.Success,
    nestedScrollConnection: NestedScrollConnection,
    flingBehavior: FlingBehavior,
    openDrawer: () -> Unit,
    reload: () -> Unit,
    summarize: () -> Unit,
    onClickedWeatherProviderButton: () -> Unit,
) {
    CustomTopAppBar(
        smallTitle = {
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp))
                    Text(
                        text = topAppBarUiState.location?.address ?: stringResource(id = R.string.unknown_address),
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
            ) {
                Text(
                    text = listOf(
                        AStyle("${topAppBarUiState.location?.address ?: stringResource(id = R.string.unknown_address)}\n",
                            span = SpanStyle(fontSize = 25.sp)),
                        AStyle(
                            topAppBarUiState.location?.country ?: stringResource(id = R.string.unknown_country),
                            span = SpanStyle(
                                fontSize = 16.sp,
                            ),
                        ),
                    ).toAnnotated(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 28.sp,
                    style = LocalTextStyle.current.merge(outlineTextStyle),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(R.drawable.ic_time).crossfade(false).build(),
                        contentDescription = stringResource(id = R.string.weather_info_head_info_update_time),
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = weatherContentUiState.dateTime,
                        fontSize = 14.sp,
                        color = Color.White, style = LocalTextStyle.current.merge(outlineTextStyle),
                    )
                }
                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onClickedWeatherProviderButton()
                    }) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(weatherContentUiState.args.weatherProvider.icon)
                            .crossfade(false).build(),
                        contentDescription = stringResource(id = R.string.weather_provider),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = weatherContentUiState.args.weatherProvider.title),
                        fontSize = 14.sp,
                        color = Color.White,
                        style = LocalTextStyle.current.merge(outlineTextStyle),
                    )

                }
            }
        },
        actions = {
            IconButton(modifier = modifier, onClick = summarize) {
                Icon(painter = painterResource(id = R.drawable.ic_shine_128_128),
                    modifier = Modifier.size(24.dp),
                    contentDescription = null)
            }
            IconButton(modifier = modifier, onClick = reload) {
                Icon(painter = painterResource(id = R.drawable.ic_refresh), modifier = Modifier.size(24.dp), contentDescription = null)
            }
        },
        scrollState = scrollState,
        flingBehavior = flingBehavior,
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

@OptIn(ExperimentalMaterial3Api::class)
private class CustomExitUntilCollapsedScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>,
    override val flingAnimationSpec: DecayAnimationSpec<Float>,
) : TopAppBarScrollBehavior {

    override val isPinned: Boolean = false

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // 아래로 스크롤하는 경우 가로채지 마세요. 손가락을 위에서 아래로 스와이프
            if (available.y > 0f) return Offset.Zero

            val prevHeightOffset = state.heightOffset
            state.heightOffset += available.y
            return if (prevHeightOffset != state.heightOffset) {
                // 상단 앱 바를 접거나 펼치는 중입니다.
                // Y축의 스크롤만 사용합니다.
                available.copy(x = 0f)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset, available: Offset, source: NestedScrollSource
        ): Offset {
            state.contentOffset += consumed.y

            if (available.y < 0f || consumed.y < 0f) {
                // 위로 스크롤할 때 상태의 높이 오프셋을 업데이트하기만 하면 됩니다.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += consumed.y
                //return Offset(0f, state.heightOffset - oldHeightOffset)
            }

            if (consumed.y == 0f && available.y > 0) {
                // 아래로 스크롤할 때 전체 콘텐츠 오프셋을 0으로 재설정합니다. 이렇게 하면 일부 플로트 정밀도의 부정확성을 제거할 수 있습니다.
                state.contentOffset = 0f
            }

            if (available.y > 0f) {
                //소비된 델타 Y가 사전 스크롤에서 사용 가능한 델타 Y로 기록된 것보다 적은 경우 높이 오프셋을 조정합니다.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += available.y
                //return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            return superConsumed /* + settleAppBar(state, available.y, flingAnimationSpec, snapAnimationSpec)*/
        }
    }
}


@Stable
data class CustomTopAppBarColors(
    val containerColor: Color = Color.Transparent,
    val scrolledContainerColor: Color = Color.Transparent,
    val navigationIconContentColor: Color = Color.White,
    val titleContentColor: Color = Color.White,
    val actionIconContentColor: Color = Color.White,
)

private val defaultCustomTopAppBarColors = CustomTopAppBarColors()