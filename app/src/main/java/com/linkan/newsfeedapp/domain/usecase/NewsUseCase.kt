package com.linkan.newsfeedapp.domain.usecase

import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.domain.repository.NewsFeedRepository
import com.linkan.newsfeedapp.util.ResultEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsUseCase @Inject constructor(
    private val repository: NewsFeedRepository
) {
    suspend fun searchNewsForEverything(searchKey: String, pageSize : Int, pageNumber : Int): Flow<ResultEvent<List<NewsArticle>>> {
        return repository.fetchNewsAboutEverythingSearchByKey(searchKey, pageSize, pageNumber)
    }
}