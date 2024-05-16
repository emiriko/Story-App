package com.example.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.dto.LoginDTO
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.preferences.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>>
        get() = _result

    fun login(body: LoginDTO) {
        viewModelScope.launch {
            _result.postValue(Result.Loading)
            val response = repository.login(body, preferences)
            _result.postValue(response)
        }
    }
}