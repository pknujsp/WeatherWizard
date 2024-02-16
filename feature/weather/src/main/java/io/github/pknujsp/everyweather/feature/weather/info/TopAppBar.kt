package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import io.github.pknujsp.everyweather.feature.weather.CustomTopAppBarColors
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TopAppBarUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    topAppBarUiState: TopAppBarUiState,
    weatherContentUiState: WeatherContentUiState.Success,
    nestedScrollConnection: NestedScrollConnection,
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
        nestedScrollConnection = nestedScrollConnection,
        colors = CustomTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
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

@ExperimentalMaterial3Api
@Composable
fun customExitUntilCollapsedScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(initialHeightOffsetLimit = -100f),
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
): TopAppBarScrollBehavior = CustomExitUntilCollapsedScrollBehavior(
    state = state,
    snapAnimationSpec = snapAnimationSpec,
    flingAnimationSpec = flingAnimationSpec,
)


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

/*
@OptIn(ExperimentalMaterial3Api::class)
suspend fun settleAppBar( collapsedFraction: Float,
    state: ScrollState, velocity: Float, flingAnimationSpec: DecayAnimationSpec<Float>, snapAnimationSpec: AnimationSpec<Float>
): Velocity {
    // 앱 바가 완전히 접히거나 확장되었는지 확인합니다. 그렇다면 앱 바를 고정할 필요가 없습니다,
    // 그냥 제로 속도를 반환합니다.
    // 붕괴된 프랙션의 부동 소수점 정밀도 때문에 0f를 확인하지 않는다는 점에 유의하세요.
    // 계산으로 0f를 확인하지 않습니다.
    if (collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // 이전 사용자 플링 후 남은 초기 속도가 있는 경우, 다음과 같이 애니메이션을 적용합니다.
    // 모션을 계속하여 앱 바를 펼치거나 접습니다.
    if (abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingAnimationSpec) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset

            state.heightOffset = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.heightOffset)

            lastValue = value
            remainingVelocity = this.velocity
            // 반올림 오류를 방지하고 소비되지 않은 항목이 있으면 중지합니다.
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    // 애니메이션 사양이 제공된 경우 스냅합니다.
    if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
        AnimationState(initialValue = state.heightOffset).animateTo(if (state.collapsedFraction < 0.5f) {
            0f
        } else {
            state.heightOffsetLimit
        }, animationSpec = snapAnimationSpec) { state.heightOffset = value }
    }

    return Velocity(0f, remainingVelocity)
}*/