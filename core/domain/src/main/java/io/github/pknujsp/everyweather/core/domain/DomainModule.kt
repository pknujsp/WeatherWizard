package io.github.pknujsp.everyweather.core.domain

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationCoordinate
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationUseCase

@InstallIn(SingletonComponent::class)
@Module
internal object DomainModule {
    @Provides
    internal fun providesGetCurrentLocationAddress(getCurrentLocationUseCase: GetCurrentLocationUseCase): GetCurrentLocationAddress =
        getCurrentLocationUseCase

    @Provides
    internal fun providesGetCurrentLocationCoordinate(getCurrentLocationUseCase: GetCurrentLocationUseCase): GetCurrentLocationCoordinate =
        getCurrentLocationUseCase
}
