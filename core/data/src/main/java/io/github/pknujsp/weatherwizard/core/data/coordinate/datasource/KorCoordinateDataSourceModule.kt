package io.github.pknujsp.weatherwizard.core.data.coordinate.datasource

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.database.coordinate.KorCoordinateDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object KorCoordinateDataSourceModule {

    @Singleton
    @Provides
    fun providesKorCoordinateDataSource(korCoordinateDao: KorCoordinateDao): KorCoordinateDataSource =
        KorCoordinateDataSourceImpl(korCoordinateDao)

}