package io.github.pknujsp.weatherwizard.core.common.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManagerImpl
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManagerImpl
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManagerImpl
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun providesAppLocationManager(@ApplicationContext context: Context): AppLocationManager = AppLocationManagerImpl(context)

    @Provides
    fun providesAppAlarmManager(@ApplicationContext context: Context): AppAlarmManager = AppAlarmManagerImpl(context)

    @Provides
    fun providesWidgetManager(@ApplicationContext context: Context): WidgetManager = WidgetManagerImpl(context)
}