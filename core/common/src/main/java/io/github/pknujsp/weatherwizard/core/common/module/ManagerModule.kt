package io.github.pknujsp.weatherwizard.core.common.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ManagerModule {

    @Provides
    @Singleton
    fun providesAppLocationManager(@ApplicationContext context: Context): AppLocationManager = AppLocationManagerImpl(context)

}