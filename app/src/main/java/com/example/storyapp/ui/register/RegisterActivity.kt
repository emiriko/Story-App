package com.example.storyapp.ui.register

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
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
                // TODO: Implement register functionality.
            }
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