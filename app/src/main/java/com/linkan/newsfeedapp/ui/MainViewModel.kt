package com.linkan.newsfeedapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.domain.usecase.NewsUseCase
import com.linkan.newsfeedapp.util.ResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
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
    private var currentPage = 1
    private val mNewsFeedState = MutableStateFlow<ResultEvent<List<NewsArticle>>>(ResultEvent.Loading)
    val newsFeedState = mNewsFeedState.asStateFlow()

    private val mErrorState = MutableSharedFlow<String?>()
    val errorState = mErrorState.asSharedFlow()

    fun searchNewsForEverythingByKey(searchKey: String, pageSize : Int = 20, pageNumber : Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            mNewsFeedState.value = ResultEvent.Loading
            newsUseCase.searchNewsForEverything(searchKey, pageSize, pageNumber)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    ResultEvent.Loading
                )
                .collectLatest {
                    when (it) {
                        is ResultEvent.Error -> mErrorState.emit(it.errorMessage)
                        is ResultEvent.Success -> {
                            if (pageNumber == 1) loadedArticles.clear()
                            loadedArticles.addAll(it.data)
                            mNewsFeedState.value = ResultEvent.Success(loadedArticles)
                        }
                        else -> {
                            mNewsFeedState.value = it
                        }
                    }
                }
        }
    }


    fun loadNextPage(searchKey: String) {
        currentPage++
        searchNewsForEverythingByKey(searchKey, currentPage)
    }
}