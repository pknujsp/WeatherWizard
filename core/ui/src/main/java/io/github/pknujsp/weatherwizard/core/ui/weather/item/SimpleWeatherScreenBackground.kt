package io.github.pknujsp.weatherwizard.core.ui.weather.item


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.ui.PlaceHolder

private val backgroundColor = Color(158, 158, 158, 255)

@Composable
private fun DefaultSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RectangleShape,
        color = backgroundColor,
    ) {
        content()
    }
}

@Composable
fun SimpleWeatherScreenBackground(cardInfo: CardInfo) {
    DefaultSurface {
        Column(modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .wrapContentHeight()) {
            Row(modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = cardInfo.title, style = TextStyle(color = Color.White, fontSize = 18.sp), modifier = Modifier.weight(1f))
                cardInfo.buttons.forEach { (text, onClick) ->
                    TextButton(onClick = onClick) {
                        Text(text = text, style = TextStyle(color = Color.White, fontSize = 14.sp))
                    }
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
    PlaceHolder(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = TextStyle(fontSize = 18.sp, color = Color.White),
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 8.dp, top = 12.dp)
            )
            Text(text = description, style = TextStyle(fontSize = 12.sp, color = Color.White))
            Button(onClick = { onClick() }, colors = ButtonDefaults.buttonColors(), modifier = Modifier
                .padding(start = 32.dp, end = 32.dp,
                    top = 6.dp, bottom = 12.dp)
            ) {
                Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.reload))
            }
        }
    }
}

data class CardInfo(
    val title: String,
    val buttons: List<Pair<String, () -> Unit>> = emptyList(),
    val content: @Composable () -> Unit
)