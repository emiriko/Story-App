package com.example.storyapp.data.remote.model

data class UserModel(
    val email: String,
    val token: String,
    val isLoggedIn: Boolean = false,
)
