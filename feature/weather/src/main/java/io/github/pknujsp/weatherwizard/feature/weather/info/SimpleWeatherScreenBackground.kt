package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val backgroundColor = Color(18, 18, 18, (0.671 * 255).toInt())

@Composable
private fun DefaultSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
    ) {
        content()
    }
}

@Composable
fun SimpleWeatherScreenBackground(cardInfo: CardInfo) {
    DefaultSurface {
        Column(modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()) {
            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(text = cardInfo.title, style = TextStyle(color = Color.White, fontSize = 18.sp), modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(8.dp))
            cardInfo.content()
        }
    }
}

data class CardInfo(
    val title: String,
    val buttons: List<Pair<String, () -> Unit>> = emptyList(),
    val content: @Composable () -> Unit
)