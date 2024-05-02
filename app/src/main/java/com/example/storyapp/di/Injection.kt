package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.ui.settings.SettingsPreferences
import com.example.storyapp.ui.settings.dataStore

object Injection {
    fun providePreferences(context: Context): SettingsPreferences {
        return SettingsPreferences.getInstance(context.dataStore)
    }
}