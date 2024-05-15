package com.example.storyapp.ui.register

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
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.dto.RegisterDTO
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.utils.capitalized
import com.google.android.material.snackbar.Snackbar


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        with(binding) {
            edRegisterEmail.addTextChangedListener {
                enableButton()
            }
            edRegisterPassword.addTextChangedListener {
                enableButton()
            }
            edRegisterName.addTextChangedListener {
                enableButton()
            }

            btnRegister.setOnClickListener {
                val body = RegisterDTO(
                    name = edRegisterName.text.toString(),
                    email = edRegisterEmail.text.toString(),
                    password = edRegisterPassword.text.toString()
                )
                hideKeyboard()
                registerUser(body)
            }
            
            viewModel.result.observe(this@RegisterActivity) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Success -> {
                            showLoading(false)
                            Snackbar.make(
                                root,
                                getString(R.string.register_success),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }

                        is Result.Loading -> {
                            showLoading(true)
                            btnRegister.isEnabled = false
                        }

                        is Result.Error -> {
                            showLoading(false)
                            enableButton()
                            Snackbar.make(
                                root,
                                result.error.capitalized(),
                                Snackbar.LENGTH_SHORT
                            ).show()
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

    private fun registerUser(body: RegisterDTO) {
        viewModel.register(body)
    }

    private fun showLoading(loading: Boolean) {
        with(binding) {
            loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            overlayView.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun enableButton() {
        with(binding) {
            btnRegister.isEnabled = when {
                edRegisterEmail.text.toString().isEmpty() -> false
                edRegisterPassword.text.toString().isEmpty() -> false
                edRegisterName.text.toString().isEmpty() -> false
                layoutRegisterEmail.error != null -> false
                layoutRegisterPassword.error != null -> false
                layoutRegisterName.error != null -> false
                else -> true
            }
        }
    }
}