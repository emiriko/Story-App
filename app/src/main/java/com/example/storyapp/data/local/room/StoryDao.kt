package com.example.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStories(stories: List<StoryEntity>)

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}