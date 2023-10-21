package io.github.pknujsp.weatherwizard.core.model.notification.enums

import io.github.pknujsp.weatherwizard.core.common.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.IEnum


enum class RefreshInterval(val interval: Long) : IEnum {
    MANUAL(0L) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.manual
    },
    MIN_15(java.time.Duration.ofMinutes(15).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.min_15
    },
    MIN_30(java.time.Duration.ofMinutes(30).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.min_30
    },
    HOUR_1(java.time.Duration.ofHours(1).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hour_1
    },
    HOUR_2(java.time.Duration.ofHours(2).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hour_2
    },
    HOUR_3(java.time.Duration.ofHours(3).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hour_3
    },
    HOUR_6(java.time.Duration.ofHours(6).toMillis()) {
        override val key: Int = ordinal
        override val title: Int = io.github.pknujsp.weatherwizard.core.common.R.string.hour_6
    };

    companion object : BaseEnum<RefreshInterval> {
        override val default: RefreshInterval = MANUAL
        override val key: String get() = "RefreshInterval"
        override val enums: Array<RefreshInterval> get() = entries.toTypedArray()

    }
}