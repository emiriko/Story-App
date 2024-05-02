package com.example.storyapp.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
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
                // TODO: Implement login functionality.
            }
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