package io.github.pknujsp.everyweather.feature.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.button.ButtonSize
import io.github.pknujsp.everyweather.core.ui.button.IconButtonSize
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.button.PrimaryIconButton
import io.github.pknujsp.everyweather.core.ui.button.SecondaryIconButton
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navigateToStart: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val currentNavigateToStart by rememberUpdatedState(newValue = navigateToStart)

    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = {
            onboardingItems.size
        })

        Text(text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.app_name),
            modifier = Modifier.padding(16.dp),
            fontSize = 24.sp,
            style = TextStyle.Default.copy(fontWeight = FontWeight.Bold))

        HorizontalPager(state = pagerState, modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) { page ->
            Card(Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f)
                .graphicsLayer {
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    alpha = lerp(start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                }) {
                when (val onboardingItem = onboardingItems[page]) {
                    is DefaultOnboardingItem -> DefaultOnBoardingItem(onboardingItem)
                    is PemissionOnboardingItem -> PermissionOnBoardingItem(onboardingItem)
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp), contentAlignment = Alignment.Center) {
            Row(Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.CenterStart), horizontalArrangement = Arrangement.Center) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.Blue else Color.LightGray
                    Box(modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(16.dp))
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                if (pagerState.currentPage > 0) {
                    SecondaryIconButton(onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                        }
                    },
                        icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_baseline_chevron_left_24,
                        iconColor = Color.Black,
                        buttonSize = IconButtonSize.LARGE)
                }
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    PrimaryIconButton(onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage((pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1))
                        }
                    },
                        icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_baseline_chevron_right_24,
                        iconColor = Color.Black,
                        buttonSize = IconButtonSize.LARGE)
                } else {
                    PrimaryButton(text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.onboarding_button_start_app),
                        buttonSize = ButtonSize.LARGE) {
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
fun DefaultOnBoardingItem(onboardingItem: DefaultOnboardingItem) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(onboardingItem.image).build(), contentDescription = null)
            Text(text = stringResource(id = onboardingItem.title),
                fontSize = 24.sp,
                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold))
            Text(text = stringResource(id = onboardingItem.message),
                fontSize = 16.sp,
                style = TextStyle.Default.copy(fontWeight = FontWeight.Normal))
        }
    }
}

@Composable
fun PermissionOnBoardingItem(onboardingItem: PemissionOnboardingItem) {

}