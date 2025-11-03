package com.linkan.newsfeedapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.domain.usecase.NewsUseCase
import com.linkan.newsfeedapp.util.ResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsUseCase: NewsUseCase
) : ViewModel() {

    private val loadedArticles = mutableListOf<NewsArticle>()
    var currentPage = 1
    var isLastPage = false
    var isLoading = false
    var isPaginating = false

    private val mNewsFeedState =
        MutableStateFlow<ResultEvent<List<NewsArticle>>>(ResultEvent.Loading)
    val newsFeedState = mNewsFeedState.asStateFlow()

    var currentSearchKey = "News"

    init {
        if (loadedArticles.isEmpty()) {
            searchNewsForEverythingByKey(currentSearchKey)
        }
    }

    /**
     * Called when user enters a new search key.
     * Clears old data & restarts pagination.
     */
    fun searchNewKeyword(searchKey: String, pageSize: Int = 20) {

        if (searchKey.isBlank() || searchKey == currentSearchKey) return

        currentSearchKey = searchKey
        currentPage = 1
        isLastPage = false
        loadedArticles.clear()
        searchNewsForEverythingByKey(searchKey, pageSize)
    }

    fun searchNewsForEverythingByKey(
        searchKey: String,
        pageSize: Int = 20,
        isLoadMore: Boolean = false
    ) {
        if (isLoading) return
        isLoading = true
        if (isLoadMore) isPaginating = true

        viewModelScope.launch(Dispatchers.IO) {
            mNewsFeedState.value = ResultEvent.Loading

            newsUseCase.searchNewsForEverything(searchKey, pageSize, currentPage)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    ResultEvent.Loading
                )
                .collectLatest { result ->
                    when (result) {
                        is ResultEvent.Success -> {
                            val articles = result.data
                            if (currentPage == 1) loadedArticles.clear()
                            loadedArticles.addAll(articles)

                            isLastPage = articles.size < pageSize
                            mNewsFeedState.value = ResultEvent.Success(loadedArticles)
                        }

                        is ResultEvent.Error -> {
                            mNewsFeedState.value = result
                        }

                        ResultEvent.Loading -> mNewsFeedState.value = result
                    }
                    isLoading = false
                    isPaginating = false
                }
        }
    }

    fun retry() {
        searchNewsForEverythingByKey(currentSearchKey)
    }

    fun loadNextPage(pageSize: Int = 20) {
        if (!isLastPage && !isLoading) {
            currentPage++
            searchNewsForEverythingByKey(currentSearchKey, pageSize, isLoadMore = true)
        }
    }
}