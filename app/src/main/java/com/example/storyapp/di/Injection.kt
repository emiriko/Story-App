package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.local.room.StoryDatabase
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
        val storyService = APIConfig.getStoryService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(storyService, storyDatabase)
    }

    fun provideUserRepository(): UserRepository {
        val token = ""
        val storyService = APIConfig.getStoryService(token)
        return UserRepository.getInstance(storyService)
    }
}