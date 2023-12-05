package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializerImpl
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryImpl
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepositoryInitializer
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScopeRepositoryModule {

    @Provides
    fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)


    @Provides
    fun providesWeatherDataRepositoryInitializer(weatherDataRepositoryImpl: WeatherDataRepositoryImpl): WeatherDataRepositoryInitializer =
        weatherDataRepositoryImpl

    @Provides
    @Singleton
    fun providesRepositoryInitializer(weatherDataRepositoryInitializer: WeatherDataRepositoryInitializer): RepositoryInitializer =
        RepositoryInitializerImpl(weatherDataRepositoryInitializer)

}