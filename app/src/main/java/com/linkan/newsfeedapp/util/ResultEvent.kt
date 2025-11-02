package com.linkan.newsfeedapp.util

sealed class ResultEvent<out T> {

    data class Success<out T>(val data: T) : ResultEvent<T>()

    data class Error(val errorMessage: String? = null, val errorCode : Int? = null) : ResultEvent<Nothing>()

    data object Loading : ResultEvent<Nothing>()
}