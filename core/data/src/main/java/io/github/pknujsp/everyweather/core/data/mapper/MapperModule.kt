package io.github.pknujsp.everyweather.core.data.mapper

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.everyweather.core.common.module.KtJson
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    @Provides
    fun provideJsonParser(
        @KtJson json: Json,
    ): JsonParser = JsonParser(json)
}
