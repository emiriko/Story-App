package com.example.storyapp.ui.upload

import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar

class UploadActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
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
        } else {
            Log.d("Photo Picker", "No media selected")
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

        if(savedInstanceState != null) {
            val savedImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI) as Uri?
            binding.previewImage.setImageURI(savedImageUri)
        }
        
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
        if(imageUri != null) {
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
            loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            overlayView.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
    private fun addNewStory() {
        val description = binding.edAddDescription.text.toString()
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            viewModel.addNewStory(imageFile, description).observe(this) {result ->
                if(result != null) {
                    when(result) {
                        is Result.Loading -> {
                            binding.buttonAdd.isEnabled = false
                            binding.btnGallery.isEnabled = false
                            binding.btnCamera.isEnabled = false
                            showLoading(true)
                        }
                        is Result.Error -> {
                            binding.buttonAdd.isEnabled = false
                            binding.btnGallery.isEnabled = false
                            binding.btnCamera.isEnabled = false
                            showLoading(false)
                            Snackbar.make(binding.root, result.error.capitalized(), Snackbar.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
        } ?: Toast.makeText(this, getString(R.string.image_missing), Toast.LENGTH_SHORT).show()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_IMAGE_URI, imageUri)
    }
    override fun onResume() {
        super.onResume()

        if(imageUri != null) {
            binding.previewImage.setImageURI(imageUri)
        }
    }
    
    companion object {
        private const val KEY_IMAGE_URI = "image_uri"
    }
}