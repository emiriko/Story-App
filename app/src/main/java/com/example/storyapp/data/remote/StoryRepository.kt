package com.example.storyapp.data.remote

import androidx.lifecycle.liveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.api.StoryService
import com.example.storyapp.data.remote.dto.LoginDTO
import com.example.storyapp.data.remote.dto.RegisterDTO
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.preferences.UserPreferences
import com.example.storyapp.utils.Helper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private val storyService: StoryService) {
    fun register(body: RegisterDTO) = liveData {
        emit(Result.Loading)
        try {
            val response = storyService.register(name = body.name, email = body.email, password = body.password)
            emit(Result.Success(response))
        } catch (error: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(error)))
        }
    }
    
    fun login(body: LoginDTO, preferences: UserPreferences) = liveData { 
        emit(Result.Loading)
        
        try {
            val response = storyService.login(email = body.email, password = body.password)
            val loginResult = response.loginResult as LoginResult
            preferences.saveUserSession(email = body.email, token = loginResult.token as String)
            emit(Result.Success(loginResult))
        } catch (error: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(error)))
        }
    }
    
    fun addNewStory(file: File, description: String) = liveData { 
        emit(Result.Loading)
        try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val response = storyService.addNewStory(description = requestBody, photo = multipartBody)
            emit(Result.Success(response))
        } catch (error: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(error)))
        } catch (error: Exception) {
            emit(Result.Error(error.message.toString()))
        }
    }
    
    fun getAllStories() = liveData {
        emit(Result.Loading)
        try {
            val response = storyService.getAllStories()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(e)))
        }
    }
    
    fun getDetailStory(id: String) = liveData {
        emit(Result.Loading)
        try {
            val response = storyService.getDetailStory(id)
            emit(Result.Success(response))
        } catch (error: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(error)))
        }
    }
    
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        
        fun getInstance(storyService: StoryService): StoryRepository = instance ?: synchronized(this) {
                instance ?: StoryRepository(storyService).also { instance = it }
            }
    }
}