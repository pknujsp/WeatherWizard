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
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.StatefulFeatureStateManagerImpl
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
    @Singleton
    fun providesAppAlarmManager(@ApplicationContext context: Context): AppAlarmManager = AppAlarmManagerImpl(context)

    @Provides
    @Singleton
    fun providesAppNetworkManager(@ApplicationContext context: Context): AppNetworkManager = AppNetworkManagerImpl(context)

    @Provides
    @Singleton
    fun providesFeatureStatusManager(
        appNetworkManager: AppNetworkManager, appLocationManager: AppLocationManager
    ): FeatureStatusManager = StatefulFeatureStateManagerImpl(appNetworkManager, appLocationManager)


    @Provides
    @Singleton
    fun providesWidgetManager(@ApplicationContext context: Context): WidgetManager = WidgetManagerImpl(context)
}