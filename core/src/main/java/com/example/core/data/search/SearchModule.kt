package com.example.core.data.search

import com.example.core.data.search.remote.SearchRemoteDataSource
import com.example.core.data.search.remote.SearchRemoteSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class SearchModule {

    @Binds
    abstract fun bindRemoteDataSource(
        remoteDataSource: SearchRemoteSource
    ): SearchRemoteDataSource

    @Binds
    abstract fun bindRepository(
        repository: SearchRepositoryImpl
    ) : SearchRepository
}