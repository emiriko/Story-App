package com.example.storyapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    
    @ColumnInfo(name = "name")
    var name: String = "",
    
    @ColumnInfo(name = "description")
    var description: String = "",
    
    @ColumnInfo(name = "photo_url")
    var photoUrl: String = "",
)