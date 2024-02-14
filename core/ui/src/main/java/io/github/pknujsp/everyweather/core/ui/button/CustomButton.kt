package io.github.pknujsp.everyweather.core.ui.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val textStyle = TextStyle(
    fontWeight = FontWeight(400),
    letterSpacing = 0.sp,
)

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier, buttonSize: ButtonSize = ButtonSize.MEDIUM, text: String, onClick: () -> Unit
) {
    BaseButton(modifier, AppButtonDefaults.primary(buttonSize), text, onClick)
}

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier, buttonSize: ButtonSize = ButtonSize.MEDIUM, text: String, onClick: () -> Unit
) {
    BaseButton(modifier, AppButtonDefaults.secondary(buttonSize), text, onClick)
}

@Composable
fun ThirdButton(
    modifier: Modifier = Modifier, buttonSize: ButtonSize = ButtonSize.MEDIUM, text: String, onClick: () -> Unit
) {
    BaseButton(modifier, AppButtonDefaults.third(buttonSize), text, onClick)
}

@Composable
private fun BaseButton(
    modifier: Modifier, appButton: AppButtonDefaults.AppButton, text: String, onClick: () -> Unit
) {
    Button(onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        colors = appButton.colors,
        border = appButton.border,
        contentPadding = appButton.size.paddingValues) {
        Text(text = text,
            minLines = 1,
            maxLines = 1,
            style = textStyle.copy(
                fontSize = appButton.size.fontSize,
            ))
    }
}

object AppButtonDefaults {
    @Composable
    fun primary(
        buttonSize: BaseButtonSize = ButtonSize.MEDIUM,
    ) = AppButton(
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
        size = buttonSize,
        border = null,
    )

    @Composable
    fun secondary(
        buttonSize: BaseButtonSize = ButtonSize.MEDIUM,
    ) = AppButton(
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
        size = buttonSize,
        border = BorderStroke(1.dp, Color.Black),
    )

    @Composable
    fun third(
        buttonSize: BaseButtonSize = ButtonSize.MEDIUM,
    ) = AppButton(
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.White),
        size = buttonSize,
        border = null,
    )

    @Immutable
    class AppButton(
        val colors: ButtonColors,
        val size: BaseButtonSize,
        val border: BorderStroke?,
    )
}

interface BaseButtonSize {
    val paddingValues: PaddingValues
    val fontSize: TextUnit
}

enum class ButtonSize(override val paddingValues: PaddingValues, override val fontSize: TextUnit) : BaseButtonSize {
    SMALL(PaddingValues(horizontal = 16.dp, vertical = 8.dp), 14.sp), MEDIUM(PaddingValues(horizontal = 24.dp, vertical = 12.dp), 15.sp),
    LARGE(PaddingValues(horizontal = 32.dp, vertical = 16.dp), 16.sp)
}