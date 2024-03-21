package io.github.pknujsp.everyweather.core.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryIconButton(
    modifier: Modifier = Modifier,
    iconColor: Color,
    buttonSize: IconButtonSize = IconButtonSize.MEDIUM,
    icon: Int,
    onClick: () -> Unit,
) {
    BaseIconButton(modifier, AppButtonDefaults.primary(buttonSize), iconColor, icon, onClick)
}

@Composable
fun SecondaryIconButton(
    modifier: Modifier = Modifier,
    iconColor: Color,
    buttonSize: IconButtonSize = IconButtonSize.MEDIUM,
    icon: Int,
    onClick: () -> Unit,
) {
    BaseIconButton(modifier, AppButtonDefaults.secondary(buttonSize), iconColor, icon, onClick)
}

@Composable
fun ThirdIconButton(
    modifier: Modifier = Modifier,
    iconColor: Color,
    buttonSize: IconButtonSize = IconButtonSize.MEDIUM,
    icon: Int,
    onClick: () -> Unit,
) {
    BaseIconButton(modifier, AppButtonDefaults.third(buttonSize), iconColor, icon, onClick)
}

@Composable
private fun BaseIconButton(
    modifier: Modifier,
    appButton: AppButtonDefaults.AppButton,
    iconColor: Color,
    icon: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .clip(RectangleShape)
                .size(appButton.size.paddingValues.calculateTopPadding())
                .background(appButton.colors.containerColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.fillMaxSize(), tint = iconColor)
    }
}

enum class IconButtonSize(override val paddingValues: PaddingValues, override val fontSize: TextUnit) : BaseButtonSize {
    SMALL(PaddingValues(horizontal = 28.dp, vertical = 28.dp), 14.sp),
    MEDIUM(PaddingValues(horizontal = 36.dp, vertical = 36.dp), 15.sp),
    LARGE(PaddingValues(horizontal = 48.dp, vertical = 48.dp), 16.sp),
}
