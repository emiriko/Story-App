package com.example.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.example.storyapp.R
import com.example.storyapp.data.remote.api.APIConfig
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.di.Injection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class StackRemoteViewsFactory(private val mContext: Context): RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = ArrayList<ListStoryItem>()
    
    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val preferences = Injection.provideUserPreferences(mContext)
        val user = runBlocking { preferences.getUserSession().first() }
        
        val service = APIConfig.getStoryService(user.token)
        
        runBlocking {
            try {
                val response = service.getAllStories()
                mWidgetItems.clear()
                mWidgetItems.addAll(response.listStory)
            } catch (error: HttpException) {
                mWidgetItems.clear()
            }
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewUri(R.id.imageView, mWidgetItems[position].photoUrl.toUri())
        val extras = bundleOf(
            StoryWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}