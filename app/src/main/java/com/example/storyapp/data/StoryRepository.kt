package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.StoryService
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.utils.Helper
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val storyService: StoryService,
    private val storyDatabase: StoryDatabase
) {
    suspend fun addNewStory(
        file: File,
        description: String,
        latitude: Double?,
        longitude: Double?
    ): Result<AddNewStoryResponse> {
        return wrapEspressoIdlingResource {
            try {
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                val response =
                    storyService.addNewStory(
                        description = requestBody,
                        photo = multipartBody,
                        latitude = latitude,
                        longitude = longitude
                    )

                Result.Success(response)
            } catch (error: HttpException) {
                Result.Error(Helper.getErrorMessage(error))
            } catch (error: Exception) {
                Result.Error(error.message.toString())
            }
        }
    }

    fun getAllPaginatedStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, storyService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
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

        fun clearInstance() {
            instance = null
        }
    }
}