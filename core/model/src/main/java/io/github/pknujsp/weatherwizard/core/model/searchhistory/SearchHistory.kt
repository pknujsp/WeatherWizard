package io.github.pknujsp.weatherwizard.core.model.searchhistory

import io.github.pknujsp.weatherwizard.core.model.UiModel

data class SearchHistory(
    val id: Long, val query: String
) : UiModel {
    var onDeleteClicked: (() -> Unit)? = null
}