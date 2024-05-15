package com.example.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detailId = intent.getStringExtra(DETAIL_ID)

        if (detailId != null) {
            viewModel.getDetailStory(detailId).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.bookImage.visibility = View.GONE
                            binding.noStoryFoundText.visibility = View.GONE
                            showLoading(true)
                        }

                        is Result.Success -> {
                            with(binding) {
                                bookImage.visibility = View.GONE
                                noStoryFoundText.visibility = View.GONE
                                Glide.with(root).load(result.data.story?.photoUrl) // URL Gambar
                                    .into(ivDetailPhoto)
                                tvDetailName.text = result.data.story?.name
                                tvDetailDescription.text = result.data.story?.description
                                showLoading(false)
                            }
                        }

                        is Result.Error -> {
                            binding.bookImage.visibility = View.VISIBLE
                            binding.noStoryFoundText.visibility = View.VISIBLE
                            showLoading(false)
                        }
                    }
                }
            }
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
        const val DETAIL_ID = "detail_id"
    }
}