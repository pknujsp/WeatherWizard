package io.github.pknujsp.weatherwizard.core.common.util

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
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

        aStyle.contentId.forEach {
            appendInlineContent(it.first)
        }
    }
}


data class AStyle(
    val text: String = "",
    val paragraph: ParagraphStyle? = null,
    val span: SpanStyle? = null,
    val contentId: List<Pair<String, InlineTextContent>> = emptyList()
) {
    val inlineContents = contentId.associate { (id, content) ->
        id to content
    }

}