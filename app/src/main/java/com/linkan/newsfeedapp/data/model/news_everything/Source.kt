package com.linkan.newsfeedapp.data.model.news_everything

import com.google.gson.annotations.SerializedName

data class Source(

    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null

)