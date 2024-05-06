package com.example.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.dto.LoginDTO
import com.example.storyapp.preferences.UserPreferences

class LoginViewModel(
    private val repository: UserRepository,
    private val preferences: UserPreferences
) : ViewModel() {
    fun login(body: LoginDTO) = repository.login(body, preferences)
}