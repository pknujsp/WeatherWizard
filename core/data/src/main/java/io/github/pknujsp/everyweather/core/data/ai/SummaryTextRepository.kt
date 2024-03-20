package io.github.pknujsp.everyweather.core.data.ai

import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

interface SummaryTextRepository {
    suspend fun generateContentStream(prompt: Prompt): Flow<GenerateContentResponse>
}

interface Prompt {
    val id: Int

    fun build(): String
}
