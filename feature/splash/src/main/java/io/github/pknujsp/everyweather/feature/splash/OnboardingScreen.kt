package io.github.pknujsp.everyweather.feature.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.button.ButtonSize
import io.github.pknujsp.everyweather.core.ui.button.IconButtonSize
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.button.PrimaryIconButton
import io.github.pknujsp.everyweather.core.ui.button.SecondaryIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navigateToStart: () -> Unit) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val currentNavigateToStart by rememberUpdatedState(newValue = navigateToStart)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        val pagerState =
            rememberPagerState(pageCount = {
                onboardingItems.size
            })

        Text(
            text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.app_name),
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            style = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
        )

        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) { page ->

            when (val onboardingItem = onboardingItems[page]) {
                is DefaultOnboardingItem -> DefaultOnBoardingItem(onboardingItem = onboardingItem)
                is PemissionOnboardingItem -> PermissionOnBoardingItem(onboardingItem)
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier
                    .align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.Black else Color.LightGray
                    Box(
                        modifier =
                            Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(14.dp),
                    )
                }
            }

            Row(modifier = Modifier.align(Alignment.CenterEnd), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (pagerState.currentPage > 0) {
                    SecondaryIconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                            }
                        },
                        icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_baseline_chevron_left_24,
                        iconColor = Color.Black,
                        buttonSize = IconButtonSize.LARGE,
                    )
                }
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    PrimaryIconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage((pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1))
                            }
                        },
                        icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_baseline_chevron_right_24,
                        iconColor = Color.White,
                        buttonSize = IconButtonSize.LARGE,
                    )
                } else {
                    PrimaryButton(
                        text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_button_start_app),
                        buttonSize = ButtonSize.MEDIUM,
                    ) {
                        viewModel.completeOnboarding()
                        coroutineScope.launch {
                            currentNavigateToStart()
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DefaultOnBoardingItem(
    modifier: Modifier = Modifier,
    onboardingItem: DefaultOnboardingItem,
) {
    Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(onboardingItem.image).build(), contentDescription = null)
            Text(
                text = stringResource(id = onboardingItem.title),
                fontSize = 24.sp,
                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(id = onboardingItem.message),
                fontSize = 16.sp,
                style = TextStyle.Default.copy(fontWeight = FontWeight.Normal),
            )
        }
    }
}

@Composable
internal fun PermissionOnBoardingItem(onboardingItem: PemissionOnboardingItem) {
}
