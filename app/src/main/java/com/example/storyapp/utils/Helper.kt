package com.example.storyapp.utils

import com.example.storyapp.data.remote.response.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

object Helper {
    fun mapLanguageToLocale(language: String): String {
        return when (language) {
            "English" -> "en"
            "Indonesia" -> "id"
            else -> "en"
        }
    }
    
    fun mapLanguageToPosition(language: String): Int {
        return when (language) {
            "en" -> 0
            "id" -> 1
            else -> 0
        }
    }
    fun getErrorMessage(error: HttpException): String {
        val jsonInString = error.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        val errorMessage = errorBody.message
        return errorMessage
    }
}