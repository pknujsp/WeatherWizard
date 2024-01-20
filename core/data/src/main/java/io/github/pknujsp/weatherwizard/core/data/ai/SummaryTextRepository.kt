package io.github.pknujsp.weatherwizard.core.data.ai

import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

interface SummaryTextRepository {
    suspend fun generateContentStream(id: Int, prompt: String): Flow<GenerateContentResponse>
}