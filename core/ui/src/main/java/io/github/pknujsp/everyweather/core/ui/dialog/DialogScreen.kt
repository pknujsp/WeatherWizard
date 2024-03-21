package io.github.pknujsp.everyweather.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton

@Composable
fun DialogScreen(
    title: String,
    message: String? = null,
    negative: String,
    positive: String,
    onClickNegative: () -> Unit,
    onClickPositive: () -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title, style = TextStyle(fontSize = 24.sp, color = Color.Black))
        Spacer(modifier = Modifier.height(16.dp))
        if (message != null) {
            Text(text = message, style = TextStyle(fontSize = 16.sp, color = Color.DarkGray))
            Spacer(modifier = Modifier.height(16.dp))
        }
        content?.invoke()
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SecondaryButton(text = negative, onClick = onClickNegative)
            PrimaryButton(text = positive, onClick = onClickPositive)
        }
    }
}