package io.github.pknujsp.weatherwizard.core.common.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoDispatcher(val coDispatcherType: CoDispatcherType, val limitedParallelism: Int = 2)

enum class CoDispatcherType {
    DEFAULT, IO, MAIN, MULTIPLE, SINGLE
}

@Module
@InstallIn(SingletonComponent::class)
object CoDispatcherModule {
    @Provides
    @CoDispatcher(CoDispatcherType.IO)
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @CoDispatcher(CoDispatcherType.DEFAULT)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @CoDispatcher(CoDispatcherType.MAIN)
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @CoDispatcher(CoDispatcherType.MULTIPLE)
    fun providesMultipleDispatcher(): CoroutineDispatcher = Dispatchers.Default.limitedParallelism(2)

    @Provides
    @CoDispatcher(CoDispatcherType.SINGLE)
    fun providesSingleDispatcher(): CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1)
}