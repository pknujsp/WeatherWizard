package io.github.pknujsp.everyweather.core.model.settings

interface PreferenceModel : IEnum

interface BasePreferenceModel<T : PreferenceModel> : BaseEnum<T>
