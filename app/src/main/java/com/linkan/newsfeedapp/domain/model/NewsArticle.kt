package com.linkan.newsfeedapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsArticle (
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
) : Parcelable