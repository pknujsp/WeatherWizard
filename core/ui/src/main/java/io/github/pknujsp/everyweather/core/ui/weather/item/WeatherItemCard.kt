package io.github.pknujsp.everyweather.core.ui.weather.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes

private val backgroundColor = Color(22, 22, 22, 190)

@Composable
private fun BackgroundCard(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = AppShapes.large,
        color = backgroundColor,
    ) {
        content()
    }
}

@Composable
fun WeatherItemCard(
    modifier: Modifier = Modifier,
    isSuccessful: () -> Boolean,
    title: String,
    onClickToCompare: (() -> Unit)? = null,
    onClickToDetail: (() -> Unit)? = null,
    onClickToRefresh: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    BackgroundCard(modifier = modifier) {
        Column(
            modifier = modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, style = TextStyle(color = Color.White, fontSize = 18.sp), modifier = Modifier.weight(1f))
                if (onClickToCompare != null) {
                    CustomTextButton(onClick = onClickToCompare, text = stringResource(id = R.string.comparison))
                }
                if (onClickToDetail != null) {
                    CustomTextButton(onClick = onClickToDetail, text = stringResource(id = R.string.detail))
                }
            }

            if (isSuccessful()) {
                content()
            } else if (onClickToRefresh != null) {
                FailedCard(onClickToRefresh)
            }
        }
    }
}

@Composable
private fun CustomTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(
        modifier = modifier
            .background(Color.Transparent, ButtonDefaults.textShape)
            .defaultMinSize(minWidth = ButtonDefaults.MinWidth, minHeight = 0.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false),
                enabled = true,
                onClick = onClick,
            ),
        propagateMinConstraints = true,
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = TextStyle(color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center))
    }
}

@Composable
private fun FailedCard(onClickToRefresh: () -> Unit) {
    val currentOnClickToRefresh by rememberUpdatedState(newValue = onClickToRefresh)
    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        SecondaryButton(onClick = currentOnClickToRefresh, text = stringResource(id = R.string.reload))
    }
}