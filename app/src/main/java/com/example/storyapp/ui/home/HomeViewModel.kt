package com.example.storyapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.StoryRepository

class HomeViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getAllStories() = storyRepository.getAllStories()
}