package io.github.pknujsp.weatherwizard.core.common.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManagerImpl
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    @Singleton
    @KtJson
    fun providesKtJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        prettyPrint = true
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class KtJson