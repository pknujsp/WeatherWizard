package io.github.pknujsp.weatherwizard.core.ui.weather.item


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.ui.PlaceHolder
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes

private val backgroundColor = Color(150, 140, 155, 215)

@Composable
private fun DefaultSurface(modifier: Modifier, content: @Composable () -> Unit) {
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
fun SimpleWeatherScreenBackground(modifier: Modifier = Modifier, cardInfo: CardInfo) {
    DefaultSurface(modifier = modifier) {
        Column(modifier = modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .wrapContentHeight()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = cardInfo.title, style = TextStyle(color = Color.White, fontSize = 18.sp), modifier = Modifier.weight(1f))
                cardInfo.buttons.forEach { (text, onClick) ->
                    CustomTextButton(onClick = onClick, text = text)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            cardInfo.content()
        }
    }
}

@Composable
fun SimpleWeatherBackgroundPlaceHolder() {
    PlaceHolder(Modifier
        .fillMaxWidth()
        .height(160.dp)
        .padding(horizontal = 12.dp, vertical = 8.dp))
}

@Composable
fun SimpleWeatherFailedBox(title: String, description: String, onClick: () -> Unit) {
    DefaultSurface(Modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 12.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = title,
                style = TextStyle(fontSize = 18.sp, color = Color.White),
                modifier = Modifier.padding(start = 32.dp, end = 32.dp))
            Text(text = description, style = TextStyle(fontSize = 12.sp, color = Color.White))
            SecondaryButton(onClick = onClick, text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.reload))
        }
    }
}

@Stable
data class CardInfo(
    val title: String, val buttons: List<Pair<String, () -> Unit>> = emptyList(), val content: @Composable () -> Unit
)


@Composable
fun CustomTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(modifier = modifier
        .background(Color.Transparent, ButtonDefaults.textShape)
        .defaultMinSize(minWidth = ButtonDefaults.MinWidth, minHeight = 0.dp)
        .clickable(interactionSource = interactionSource, indication = rememberRipple(bounded = false), enabled = true, onClick = onClick),
        propagateMinConstraints = true,
        contentAlignment = Alignment.Center) {
        Text(text = text, style = TextStyle(color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center))
    }
}