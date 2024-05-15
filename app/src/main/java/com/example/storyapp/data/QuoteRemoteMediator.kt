package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.room.StoryDatabase
import com.example.storyapp.data.remote.api.StoryService

@OptIn(ExperimentalPagingApi::class)
class QuoteRemoteMediator(
    private val database: StoryDatabase,
    private val storyService: StoryService 
) : RemoteMediator<Int, StoryEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        TODO("Not yet implemented")
    }

}