package io.github.pknujsp.weatherwizard.core.common.util

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

fun List<AStyle>.toAnnotated() = buildAnnotatedString {
    forEach { aStyle ->
        aStyle.span?.also {
            withStyle(it) { append(aStyle.text) }
        } ?: aStyle.paragraph?.also {
            withStyle(it) { append(aStyle.text) }
        } ?: append(aStyle.text)
    }
}


data class AStyle(
    val text: String,
    val paragraph: ParagraphStyle? = null,
    val span: SpanStyle? = null,
)