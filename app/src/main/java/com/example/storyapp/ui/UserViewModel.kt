package com.example.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.model.UserModel
import com.example.storyapp.preferences.UserPreferences
import kotlinx.coroutines.launch

class UserViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getUserSession(): LiveData<UserModel> {
        return preferences.getUserSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preferences.logout()
        }
    }

    fun invalidateRepository() {
        StoryRepository.clearInstance()
    }
}