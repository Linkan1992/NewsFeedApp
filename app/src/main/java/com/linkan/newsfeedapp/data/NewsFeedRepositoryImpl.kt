package com.linkan.newsfeedapp.data

import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.domain.repository.NewsFeedRepository
import com.linkan.newsfeedapp.util.ResultEvent
import com.linkan.newsfeedapp.util.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsFeedRepositoryImpl @Inject constructor (
    private val newsFeedService: NewsFeedService
) : NewsFeedRepository {

    override suspend fun fetchNewsAboutEverythingSearchByKey(searchKey: String, pageSize : Int, pageNumber : Int): Flow<ResultEvent<List<NewsArticle>>> {
        return flow {
            val response = safeApiCall {
                newsFeedService.searchNewsForEverything(
                    searchQuery = searchKey,
                    pageSize = 10,
                    pageNumber = 1
                )
            }.run {
                when (this) {
                    is ResultEvent.Success -> {
                        val newsArticleList : MutableList<NewsArticle> = mutableListOf<NewsArticle>()
                        this.data.articles.forEach { model ->
                            newsArticleList.add(
                                NewsArticle(
                                    title = model.title,
                                    description = model.description,
                                    imageUrl = model.urlToImage
                                )
                            )
                        }
                        ResultEvent.Success(newsArticleList)
                    }
                    else -> this
                }
            }

            emit(response as ResultEvent<List<NewsArticle>>)
        }
    }
}