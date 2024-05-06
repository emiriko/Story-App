package com.example.storyapp.ui.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentSettingsBinding
import com.example.storyapp.ui.UserViewModel
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.utils.Helper
import java.util.Locale


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val userViewModel by viewModels<UserViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dropdown
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.language_item)
        )
        var check = 0

        binding.language.adapter = adapter

        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchTheme.isChecked = isDarkModeActive

            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        settingsViewModel.getLanguageSettings().observe(viewLifecycleOwner) { language ->
            binding.language.setSelection(Helper.mapLanguageToPosition(language))
        }

        with(binding) {
            switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                settingsViewModel.saveThemeSetting(isChecked)
            }

            actionLogout.setOnClickListener {
                val scale: Animator = ObjectAnimator.ofPropertyValuesHolder(
                    it,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f, 1f)
                )
                scale.setDuration(1000)
                scale.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        userViewModel.logout()
                    }
                })
                scale.start()
            }

            language.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?,
                    arg1: View?,
                    arg2: Int,
                    arg3: Long
                ) {
                    if (check < 1) {
                        check++
                        return
                    }
                    val item = language.selectedItem.toString()
                    val language = Helper.mapLanguageToLocale(item)
                    settingsViewModel.saveLanguageSetting(language)
                    val locale = Locale(language)
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.create(locale)
                    )
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {}
            }
        }
    }

}