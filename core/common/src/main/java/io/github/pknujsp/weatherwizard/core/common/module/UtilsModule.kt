package io.github.pknujsp.weatherwizard.core.common.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    @KtJson
    fun providesKtJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class KtJson