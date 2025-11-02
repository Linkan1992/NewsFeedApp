package com.linkan.newsfeedapp.data

import com.linkan.newsfeedapp.data.model.news_everything.NewsResponse
import com.linkan.newsfeedapp.util.Util
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsFeedService {
    @GET("everything")
    suspend fun searchNewsForEverything(
        @Query("q") searchQuery: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") pageNumber: Int,
        @Query("apiKey") apiKey: String = Util.API_KEY,
    ): Response<NewsResponse>

}