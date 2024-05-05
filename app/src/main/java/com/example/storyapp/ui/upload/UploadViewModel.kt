package com.example.storyapp.ui.upload

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.StoryRepository
import java.io.File

class UploadViewModel(private val repository: StoryRepository): ViewModel() {
    fun addNewStory(file: File, description: String) = repository.addNewStory(file, description)
}