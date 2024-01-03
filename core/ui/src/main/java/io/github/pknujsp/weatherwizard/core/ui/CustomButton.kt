package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    buttonSize: ButtonSize = ButtonSize.MEDIUM,
    text: String,
    onClick: () -> Unit
) {
    BaseButton(modifier, buttonSize.paddingValues, buttonSize.fontSize, AppButtons.primary(), Color.White, text, onClick)
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier, buttonSize: ButtonSize = ButtonSize.MEDIUM, text: String, onClick: () -> Unit
) {
    BaseButton(modifier, buttonSize.paddingValues, buttonSize.fontSize, AppButtons.secondary(), Color.Black, text, onClick)
}

@Composable
fun ThirdButton(
    modifier: Modifier = Modifier, buttonSize: ButtonSize = ButtonSize.MEDIUM, text: String, onClick: () -> Unit
) {
    BaseButton(modifier, buttonSize.paddingValues, buttonSize.fontSize, AppButtons.third(), Color.White, text, onClick)
}

@Composable
private fun BaseButton(
    modifier: Modifier,
    contentPadding: PaddingValues,
    fontSize: TextUnit,
    colors: ButtonColors,
    textColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        colors = colors,
        border = BorderStroke(1.dp, Color.Black),
        contentPadding = contentPadding) {
        Text(text = text,
            minLines = 1,
            maxLines = 1,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight(400),
                color = textColor,
                letterSpacing = 0.sp,
            ))
    }
}

object AppButtons {
    @Composable
    fun primary(
    ) = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)

    @Composable
    fun secondary(
    ) = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)

    @Composable
    fun third(
    ) = ButtonDefaults.buttonColors(containerColor = Color.Gray, contentColor = Color.White)
}

enum class ButtonSize(val paddingValues: PaddingValues, val fontSize: TextUnit) {
    SMALL(PaddingValues(horizontal = 16.dp, vertical = 8.dp), 14.sp), MEDIUM(PaddingValues(horizontal = 24.dp, vertical = 12.dp), 15.sp),
    LARGE(PaddingValues(horizontal = 32.dp, vertical = 16.dp), 16.sp)
}