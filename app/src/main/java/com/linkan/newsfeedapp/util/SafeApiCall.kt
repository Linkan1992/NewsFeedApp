package com.linkan.newsfeedapp.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

suspend fun <T> safeApiCall(apiFun: suspend () -> Response<T>?): ResultEvent<T> {

    try {
        val response = apiFun.invoke()
        response?.takeIf { it.isSuccessful && it.body() != null }
            ?.body()
            ?.let { body -> return ResultEvent.Success(data = body)
        }

        return ResultEvent.Error(response?.message() ?: "Error", null)
    } catch (exe: Exception) {
        return ResultEvent.Error(exe.message ?: "No Data", null)
    }
}

fun <T> CoroutineScope.runOnBackgroundDispatcher(
    startLoader : () -> Unit,
    backgroundFunc : suspend () -> T,
    callback : (T) -> Unit) {

    startLoader.invoke()
    this.launch(Dispatchers.IO) {
        Log.d("safeApiCall", "I'm working on Thread = ${Thread.currentThread().name}")
        val result = backgroundFunc.invoke()
        withContext(Dispatchers.Main){
            Log.d("safeApiCall", "I'm working on Thread = ${Thread.currentThread().name}")
            callback.invoke(result)
        }
    }
}