package com.dicoding.storyapp.data.local.paging

import com.dicoding.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(token: String): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService, token)
    }
}