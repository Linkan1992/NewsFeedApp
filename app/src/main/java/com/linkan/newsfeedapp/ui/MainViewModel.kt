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

    private val mNewsFeedState = MutableStateFlow<ResultEvent<List<NewsArticle>>>(ResultEvent.Loading)
    val newsFeedState = mNewsFeedState.asStateFlow()

    private val mErrorState = MutableSharedFlow<ResultEvent<List<NewsArticle>>>()
    val errorState = mErrorState.asSharedFlow()

    fun searchNewsForEverythingByKey(searchKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mNewsFeedState.value = ResultEvent.Loading
            newsUseCase.searchNewsForEverything(searchKey)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                    ResultEvent.Loading
                )
                .collectLatest {
                    when (it) {
                        is ResultEvent.Error -> mErrorState.emit(it)
                        else -> {
                            mNewsFeedState.value = it
                        }
                    }
                }

        }
    }
}