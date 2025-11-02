package com.linkan.newsfeedapp.data.model.news_everything

import com.google.gson.annotations.SerializedName;

data class NewsResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalResults") var totalResults: Int? = null,
    @SerializedName("articles") var articles: ArrayList<Articles> = arrayListOf()
)