package com.linkan.newsfeedapp.data.model.news_everything

import com.google.gson.annotations.SerializedName


data class Articles(

    @SerializedName("source") val source: Source? = Source(),
    @SerializedName("author") val author: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("urlToImage") val urlToImage: String? = null,
    @SerializedName("publishedAt") val publishedAt: String? = null,
    @SerializedName("content") val content: String? = null

)
