package com.example.storyapp.ui.upload

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.utils.capitalized
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar

class UploadActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val binding by lazy {
        ActivityUploadBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
            enableButton()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            showImage()
            enableButton()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar)

        val tvTitle = supportActionBar?.customView?.findViewById<TextView>(R.id.tvTitle)
        tvTitle?.text = getString(R.string.upload)

        if (savedInstanceState != null) {
            val savedImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI) as Uri?
            binding.previewImage.setImageURI(savedImageUri)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        with(binding) {
            edAddDescription.addTextChangedListener {
                enableButton()
            }

            btnGallery.setOnClickListener {
                startGallery()
            }

            btnCamera.setOnClickListener {
                startCamera()
            }

            buttonAdd.setOnClickListener {
                addNewStory()
            }
            
            viewModel.result.observe(this@UploadActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            buttonAdd.isEnabled = false
                            btnGallery.isEnabled = false
                            btnCamera.isEnabled = false
                            showLoading(true)
                        }

                        is Result.Error -> {
                            buttonAdd.isEnabled = false
                            btnGallery.isEnabled = false
                            btnCamera.isEnabled = false
                            showLoading(false)
                            Snackbar.make(
                                root,
                                result.error.capitalized(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Result.Success -> {
                            showLoading(false)
                            val intent = Intent(this@UploadActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        imageUri = getImageUri(this)
        launcherIntentCamera.launch(imageUri)
    }

    private fun showImage() {
        if (imageUri != null) {
            binding.previewImage.setImageURI(imageUri)
            hideKeyboard()
        }
    }

    private fun enableButton() {
        with(binding) {
            buttonAdd.isEnabled = when {
                imageUri == null -> false
                edAddDescription.text.toString().isEmpty() -> false
                else -> true
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        with(binding) {
            buttonAdd.isEnabled = false
            btnGallery.isEnabled = false
            btnCamera.isEnabled = false
            loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            overlayView.visibility = if (loading) View.VISIBLE else View.GONE
            
        }
    }
    
    private fun addNewStory() {
        with(binding) {
            if(gps.isChecked) {
                getMyLocation()
            } else {
                uploadStory()
            }
        }
    }
    private fun uploadStory(latitude: Double? = null, longitude: Double? = null) {
        with(binding) {
            val description = edAddDescription.text.toString()
            imageUri?.let { uri ->
                val imageFile = uriToFile(uri, this@UploadActivity).reduceFileImage()
                viewModel.addNewStory(imageFile, description, latitude, longitude)
            } ?: Toast.makeText(this@UploadActivity, getString(R.string.image_missing), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                LOCATION_FINE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showLoading(true)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }).addOnSuccessListener {location: Location? -> 
                if(location != null) {
                    uploadStory(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(LOCATION_FINE_PERMISSION)
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_IMAGE_URI, imageUri)
    }

    override fun onResume() {
        super.onResume()

        if (imageUri != null) {
            binding.previewImage.setImageURI(imageUri)
        }
    }

    companion object {
        private const val KEY_IMAGE_URI = "image_uri"
        private const val LOCATION_FINE_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
    }
}