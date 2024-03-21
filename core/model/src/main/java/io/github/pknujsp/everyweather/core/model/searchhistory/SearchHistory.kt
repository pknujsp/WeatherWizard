package io.github.pknujsp.everyweather.core.model.searchhistory

import io.github.pknujsp.everyweather.core.model.UiModel

data class SearchHistory(
    val id: Long,
    val query: String,
) : UiModel {
    var onDeleteClicked: (() -> Unit)? = null
}
