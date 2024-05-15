package com.example.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.UserRepository
import com.example.storyapp.data.remote.dto.RegisterDTO

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    fun register(body: RegisterDTO) = repository.register(body)
}