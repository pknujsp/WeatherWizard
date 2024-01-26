package io.github.pknujsp.weatherwizard.core.data

interface RepositoryInitializer {
    suspend fun initialize()
}