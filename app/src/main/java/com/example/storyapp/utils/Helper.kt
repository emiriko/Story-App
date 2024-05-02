package com.example.storyapp.utils

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
}