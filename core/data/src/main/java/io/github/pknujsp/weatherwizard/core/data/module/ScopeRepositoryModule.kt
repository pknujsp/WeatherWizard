package io.github.pknujsp.weatherwizard.core.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepository
import io.github.pknujsp.weatherwizard.core.data.searchhistory.SearchHistoryRepositoryImpl
import io.github.pknujsp.weatherwizard.core.database.searchhistory.SearchHistoryLocalDataSource

@Module
@InstallIn(ViewModelComponent::class)
class ScopeRepositoryModule {
    @Provides
    @ViewModelScoped
    fun providesSearchHistoryRepository(searchHistoryLocalDataSource: SearchHistoryLocalDataSource): SearchHistoryRepository =
        SearchHistoryRepositoryImpl(searchHistoryLocalDataSource)

}