package com.example.storyapp.data

import androidx.lifecycle.liveData
import com.example.storyapp.data.remote.api.StoryService
import com.example.storyapp.data.remote.dto.LoginDTO
import com.example.storyapp.data.remote.dto.RegisterDTO
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.LoginResult
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.preferences.UserPreferences
import com.example.storyapp.utils.Helper
import retrofit2.HttpException

class UserRepository private constructor(private val storyService: StoryService) {
    suspend fun register(body: RegisterDTO): Result<RegisterResponse> {
        return try {
            val response = storyService.register(
                name = body.name,
                email = body.email,
                password = body.password
            )
            Result.Success(response)
        } catch (error: HttpException) {
            Result.Error(Helper.getErrorMessage(error))
        }
    }

    suspend fun login(body: LoginDTO, preferences: UserPreferences): Result<LoginResponse> {
        return try {
            val response = storyService.login(email = body.email, password = body.password)
            val loginResult = response.loginResult as LoginResult
            preferences.saveUserSession(email = body.email, token = loginResult.token as String)

            Result.Success(response)
        } catch (error: HttpException) {
            Result.Error(Helper.getErrorMessage(error))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(storyService: StoryService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(storyService).also { instance = it }
            }
    }
}