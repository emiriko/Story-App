package com.example.storyapp.ui.upload

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import java.io.File

class UploadViewModel(private val repository: StoryRepository) : ViewModel() {
    fun addNewStory(file: File, description: String, lat: Double? = null, lon: Double? = null) = repository.addNewStory(file, description, lat, lon)
}