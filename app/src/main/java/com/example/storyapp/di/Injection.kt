package com.example.storyapp.di

import android.content.Context
import android.util.Log
import com.example.storyapp.data.remote.StoryRepository
import com.example.storyapp.data.remote.api.APIConfig
import com.example.storyapp.preferences.SettingsPreferences
import com.example.storyapp.preferences.UserPreferences
import com.example.storyapp.preferences.dataStore
import com.example.storyapp.preferences.userDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideSettingsPreferences(context: Context): SettingsPreferences {
        return SettingsPreferences.getInstance(context.dataStore)
    }

    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.userDataStore)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val preferences = provideUserPreferences(context)
        val user = runBlocking { preferences.getUserSession().first() }
        Log.d("Injection", "provideStoryRepository: ${user.token}")
        val storyService = APIConfig.getStoryService(user.token)
        Log.d("Injection", "provideStoryRepository: ${storyService.toString()}")
        return StoryRepository.getInstance(storyService)
    }
}