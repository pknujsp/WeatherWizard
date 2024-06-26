package io.github.pknujsp.everyweather.core.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.github.pknujsp.everyweather.core.data.RepositoryCacheManager
import io.github.pknujsp.everyweather.core.data.cache.CacheCleaner
import io.github.pknujsp.everyweather.core.data.cache.CacheManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

internal class SummaryTextRepositoryImpl(
    private val genModel: GenerativeModel,
    cacheManager: CacheManager<Int, List<GenerateContentResponse>>,
    cacheCleaner: CacheCleaner,
) : SummaryTextRepository, RepositoryCacheManager<Int, List<GenerateContentResponse>>(cacheCleaner, cacheManager) {
    private val interval = 100L

    override suspend fun generateContentStream(prompt: Prompt): Flow<GenerateContentResponse> {
        val cache = cacheManager.get(prompt.id)
        if (cache is CacheManager.CacheState.Hit) {
            return flow {
                for (value in cache.value) {
                    emit(value)
                    delay(interval)
                }
            }
        }

        val response: MutableList<GenerateContentResponse> = mutableListOf()
        val flow =
            genModel.generateContentStream(prompt.build()).onEach { generateContentResponse ->
                response.add(generateContentResponse)
            }.catch {
                response.clear()
            }.onCompletion {
                if (it == null && response.isNotEmpty()) {
                    cacheManager.put(prompt.id, response)
                }
            }

        return flow
    }
}
