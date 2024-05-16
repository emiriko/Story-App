package com.example.storyapp.utils

import com.example.storyapp.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                id = i.toString(),
                name = "author + $i",
                description = "quote $i",
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png"
            )
            items.add(quote)
        }
        
        return items
    }
}