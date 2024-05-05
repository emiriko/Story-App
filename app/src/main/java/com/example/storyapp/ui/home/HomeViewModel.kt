package com.example.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.model.UserModel
import com.example.storyapp.preferences.UserPreferences
import kotlinx.coroutines.launch

class HomeViewModel(
    private val storyRepository: StoryRepository,
    private val storyPreferences: UserPreferences
) : ViewModel() {

    fun getAllStories() = storyRepository.getAllStories()
    fun getUserSession(): LiveData<UserModel> {
        return storyPreferences.getUserSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyPreferences.logout()
        }
    }
}