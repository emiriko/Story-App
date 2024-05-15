package com.example.storyapp.data

import androidx.lifecycle.liveData
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.StoryService
import com.example.storyapp.utils.Helper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(private val storyService: StoryService, private val storyDatabase: StoryDatabase) {
    fun addNewStory(file: File, description: String, latitude: Double?, longitude: Double?) = liveData {
        emit(Result.Loading)
        try {
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val response =
                storyService.addNewStory(description = requestBody, photo = multipartBody, latitude = latitude, longitude = longitude)
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
    
    fun getAllStoriesWithLocation() = liveData {
        emit(Result.Loading)
        try {
            val response = storyService.getAllStories(location = 1)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(Helper.getErrorMessage(e)))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyService: StoryService, storyDatabase: StoryDatabase): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyService, storyDatabase).also { instance = it }
            }
    }
}