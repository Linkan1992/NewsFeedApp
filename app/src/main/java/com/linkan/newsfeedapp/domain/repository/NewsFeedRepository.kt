package com.linkan.newsfeedapp.domain.repository

import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.util.ResultEvent
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {
    suspend fun fetchNewsAboutEverythingSearchByKey(searchKey: String): Flow<ResultEvent<List<NewsArticle>>>
}