package com.linkan.newsfeedapp.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.linkan.newsfeedapp.BuildConfig
import com.linkan.newsfeedapp.R
import com.linkan.newsfeedapp.data.NewsFeedService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun injectGson() : Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun injectGsonConverterFactory() : GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun injectRetrofitApi(gsonConverterFactory: GsonConverterFactory) : NewsFeedService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(NewsFeedService::class.java)
    }

    @Provides
    @Singleton
    fun injectGlide(@ApplicationContext context: Context) : RequestManager = Glide.with(context).
    setDefaultRequestOptions(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_launcher_background))

}