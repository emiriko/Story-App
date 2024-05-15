package com.example.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.dto.RegisterDTO
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    private val _result = MutableLiveData<Result<RegisterResponse>>()
    val result: LiveData<Result<RegisterResponse>>
        get() = _result
    fun register(body: RegisterDTO) {
        viewModelScope.launch {
            _result.postValue(Result.Loading)
            val response = repository.register(body)
            _result.postValue(response)
        }
    }
}