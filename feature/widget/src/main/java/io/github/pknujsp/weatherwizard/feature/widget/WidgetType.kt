package io.github.pknujsp.weatherwizard.feature.widget

enum class WidgetType {
    SUMMARY;

    companion object {
        fun fromOrdinal(ordinal: Int): WidgetType {
            return entries[ordinal]
        }
    }
}