package com.dicoding.storyapp.ui.story.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.local.paging.Injection
import com.dicoding.storyapp.data.local.paging.StoryRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem

class PagingViewModel(storyRepository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}

class PagingViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PagingViewModel(Injection.provideRepository(token)) as T
        }
        throw IllegalArgumentException("Unkwiwn ViewModel class")

    }
}