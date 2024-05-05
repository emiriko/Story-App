package com.example.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.dto.RegisterDTO

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(body: RegisterDTO) = repository.register(body)
}