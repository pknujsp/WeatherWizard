package io.github.pknujsp.everyweather.core.common.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object LocaleModule {

    @Provides
    @Singleton
    @AppLocale
    fun provideLocale(@ApplicationContext context: Context): java.util.Locale {
        return context.resources.configuration.locales[0]
    }

}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppLocale