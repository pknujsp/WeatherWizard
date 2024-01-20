package io.github.pknujsp.weatherwizard.core.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

internal class SummaryTextRepositoryImpl(
    private val genModel: GenerativeModel, cacheManager: CacheManager<Int, GenerateContentResponse>, cacheCleaner: CacheCleaner
) : SummaryTextRepository, RepositoryCacheManager<Int, GenerateContentResponse>(cacheCleaner, cacheManager) {

    override suspend fun generateContentStream(prompt: Prompt): Flow<GenerateContentResponse> {
        val cache = cacheManager.get(prompt.id)
        if (cache is CacheManager.CacheState.Hit) {
            return flowOf(cache.value)
        }

        var response: GenerateContentResponse? = null
        val flow = genModel.generateContentStream(prompt.build()).onEach { generateContentResponse ->
            response = generateContentResponse
        }.onCompletion {
            if (it == null && response != null) {
                cacheManager.put(prompt.id, response!!)
            }
        }

        return flow
    }
}