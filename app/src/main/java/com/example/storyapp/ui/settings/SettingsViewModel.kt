package com.example.storyapp.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferences: SettingsPreferences): ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        
        return preferences.getThemeSetting().asLiveData()
    }
    
    fun getLanguageSettings(): LiveData<String> {
        return preferences.getLanguageSetting().asLiveData()
    }
    
    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            preferences.saveThemeSetting(isDarkModeActive)
        }
    }
    
    fun saveLanguageSetting(language: String) {
        viewModelScope.launch { 
            preferences.saveLanguageSetting(language)
        }
    }
}