package com.example.storyapp.ui.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.viewModels
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.model.UserModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.ui.UserViewModel
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.home.HomeViewModel
import com.example.storyapp.ui.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    
    private var user: UserModel? = null
    private var mapStyle = R.raw.map_light_style
    
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    
    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val tvTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = getString(R.string.maps)
        
        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            mapStyle = if (isDarkModeActive) {
                R.raw.map_dark_style
            } else {
                R.raw.map_light_style
            }
        }
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply { 
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isRotateGesturesEnabled = true
            isTiltGesturesEnabled = true
        }
        setMapStyle()
        mapsViewModel.getAllStoriesWithLocation().observe(this) { result -> 
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                    }
                    is Result.Success -> {
                        result.data.listStory.forEach { story ->
                            val latLng = LatLng(story.lat as Double, story.lon as Double)
                            mMap.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(story.description))
                        }
                        showLoading(false)
                    }
                }
            }
        }
        
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapStyle))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: $e")
        }
    }
    private fun showLoading(loading: Boolean) {
        with(binding) {
            loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            overlayView.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    
    companion object {
        private const val TAG = "MapsActivity"
    }
}