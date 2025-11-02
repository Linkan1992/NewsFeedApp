package com.linkan.newsfeedapp.di

import com.linkan.newsfeedapp.data.NewsFeedRepositoryImpl
import com.linkan.newsfeedapp.domain.repository.NewsFeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsFeedAppModule {
    @Binds
    @Singleton
    abstract fun bindNewsFeedRepository(newsFeedRepositoryImpl: NewsFeedRepositoryImpl) : NewsFeedRepository
}