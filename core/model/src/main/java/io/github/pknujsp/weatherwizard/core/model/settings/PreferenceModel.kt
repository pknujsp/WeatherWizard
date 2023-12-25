package io.github.pknujsp.weatherwizard.core.model.settings

interface PreferenceModel : IEnum {}

interface BasePreferenceModel<T : PreferenceModel> : BaseEnum<T> {}