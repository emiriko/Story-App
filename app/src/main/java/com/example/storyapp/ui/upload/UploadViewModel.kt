package com.example.storyapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.AddNewStoryResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _result = MutableLiveData<Result<AddNewStoryResponse>>()
    val result: LiveData<Result<AddNewStoryResponse>>
        get() = _result
    
    fun addNewStory(file: File, description: String, lat: Double? = null, lon: Double? = null) {
        viewModelScope.launch {
            _result.postValue(Result.Loading)
            val response = repository.addNewStory(file, description, lat, lon)
            _result.postValue(response)
        }
    }
}