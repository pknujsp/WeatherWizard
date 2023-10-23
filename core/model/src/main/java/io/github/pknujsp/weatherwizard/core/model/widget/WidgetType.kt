package io.github.pknujsp.weatherwizard.core.model.widget

enum class WidgetType {
    SUMMARY;

    companion object {
        fun fromOrdinal(ordinal: Int): WidgetType {
            return entries[ordinal]
        }
    }
}