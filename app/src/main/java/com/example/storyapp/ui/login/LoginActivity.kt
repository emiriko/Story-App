package com.example.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.dto.LoginDTO
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.utils.capitalized
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        with(binding) {
            edLoginEmail.addTextChangedListener {
                enableButton()
            }
            edLoginPassword.addTextChangedListener {
                enableButton()
            }

            btnLogin.setOnClickListener {
                val body = LoginDTO(
                    email = edLoginEmail.text.toString(),
                    password = edLoginPassword.text.toString()
                )
                hideKeyboard()
                login(body)
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

    private fun login(body: LoginDTO) {
        viewModel.login(body).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Error -> {
                        showLoading(false)
                        Snackbar.make(
                            binding.root,
                            result.error.capitalized(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        enableButton()
                    }

                    is Result.Loading -> {
                        binding.btnLogin.isEnabled = false
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
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

    private fun enableButton() {
        with(binding) {
            btnLogin.isEnabled = when {
                edLoginEmail.text.toString().isEmpty() -> false
                edLoginPassword.text.toString().isEmpty() -> false
                else -> true
            }
        }
    }
}