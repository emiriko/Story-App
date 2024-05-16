package com.example.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository

class HomeViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getAllStories() = storyRepository.getAllPaginatedStories().cachedIn(viewModelScope)
}