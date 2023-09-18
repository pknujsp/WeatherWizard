package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme


@Composable
fun RoundedButton(
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor =
    AppColorScheme.primary,
        contentColor = Color.White), text: String, onClick: () -> Unit
) {
    BaseButton(colors, text, onClick)
}

@Composable
private fun BaseButton(colors: ButtonColors, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(),
        colors = colors,
        border = BorderStroke(0.dp, Color.Transparent),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, minLines = 1, maxLines = 1)
    }
}