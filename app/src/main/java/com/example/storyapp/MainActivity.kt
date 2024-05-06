package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.UserViewModel
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.onboarding.OnboardingActivity
import com.example.storyapp.ui.settings.SettingsViewModel
import com.example.storyapp.ui.upload.UploadActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val userViewModel by viewModels<UserViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        userViewModel.getUserSession().observe(this) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
        }


        val navView: BottomNavigationView = binding.navView
        val radius = 40f

        val bottomAppBar = binding.bottomAppBar
        val shapeDrawable: MaterialShapeDrawable = bottomAppBar.background as MaterialShapeDrawable
        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_settings
            )
        )

        navView.background = null
        navView.menu.getItem(1).isEnabled = false
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar)
        val tvTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.tvTitle)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    tvTitle?.text = getString(R.string.title_home)
                }

                R.id.navigation_settings -> {
                    tvTitle?.text = getString(R.string.settings)
                }

                else -> {
                    tvTitle?.text = getString(R.string.app_name)
                }
            }
        }
    }
}