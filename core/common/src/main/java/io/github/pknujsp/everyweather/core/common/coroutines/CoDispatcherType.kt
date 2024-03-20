package io.github.pknujsp.everyweather.core.common.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoDispatcher(val coDispatcherType: CoDispatcherType)

enum class CoDispatcherType {
    DEFAULT,
    IO,
    MAIN,
    SINGLE,
}

@Module
@InstallIn(SingletonComponent::class)
internal object CoDispatcherModule {
    @Provides
    @CoDispatcher(CoDispatcherType.IO)
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @CoDispatcher(CoDispatcherType.DEFAULT)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @CoDispatcher(CoDispatcherType.MAIN)
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @Provides
    @CoDispatcher(CoDispatcherType.SINGLE)
    fun providesSingleDispatcher(): CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
}
