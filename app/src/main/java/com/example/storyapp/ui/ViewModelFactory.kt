package com.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.detail.DetailViewModel
import com.example.storyapp.ui.home.HomeViewModel
import com.example.storyapp.ui.login.LoginViewModel
import com.example.storyapp.ui.register.RegisterViewModel
import com.example.storyapp.ui.settings.SettingsViewModel
import com.example.storyapp.ui.upload.UploadViewModel

class ViewModelFactory private constructor(private val applicationContext: Context) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        SettingsViewModel::class.java -> SettingsViewModel(
            Injection.provideSettingsPreferences(applicationContext)
        ) as T
        RegisterViewModel::class.java -> RegisterViewModel(
            Injection.provideStoryRepository(applicationContext)
        ) as T
        UploadViewModel::class.java -> UploadViewModel(
            Injection.provideStoryRepository(applicationContext)
        ) as T
        DetailViewModel::class.java -> DetailViewModel(
            Injection.provideStoryRepository(applicationContext)
        ) as T
        HomeViewModel::class.java -> HomeViewModel(
            Injection.provideStoryRepository(applicationContext), Injection.provideUserPreferences(applicationContext)
        ) as T
        LoginViewModel::class.java -> LoginViewModel(
            Injection.provideStoryRepository(applicationContext), Injection.provideUserPreferences(applicationContext)
        ) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(context.applicationContext)
            }.also { instance = it }
    }
}