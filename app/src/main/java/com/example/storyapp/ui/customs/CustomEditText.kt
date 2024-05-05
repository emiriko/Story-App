package com.example.storyapp.ui.customs

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string = s.toString()
                if (string.isNotEmpty()) {
                    clearError()
                    when (id) {
                        R.id.ed_register_email -> validateEmail(string)
                        R.id.ed_register_password -> validatePassword(string)
                    }
                } else {
                    val errorMessage = context.getString(R.string.empty_field_error)
                    showError(errorMessage)
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            val errorMessage = context.getString(R.string.password_error)
            showError(errorMessage)
        } else {
            clearError()
        }
    }

    private fun validateEmail(email: String) {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            clearError()
        } else {
            val errorMessage = context.getString(R.string.email_error)
            showError(errorMessage)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showError(message: String) {
        val textInputLayout = parent.parent as? TextInputLayout
        textInputLayout?.error = message
    }

    private fun clearError() {
        val textInputLayout = parent.parent as? TextInputLayout
        textInputLayout?.error = null
    }
}