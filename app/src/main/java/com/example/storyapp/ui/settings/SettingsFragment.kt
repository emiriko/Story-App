package com.example.storyapp.ui.settings

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentSettingsBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.utils.Helper
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

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
        
        val factory = ViewModelFactory.getInstance(requireContext())
        
        val settingsViewModel: SettingsViewModel by viewModels {
            factory
        }

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
            
            
            language.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    arg0: AdapterView<*>?,
                    arg1: View?,
                    arg2: Int,
                    arg3: Long
                ) {
                    if(check < 1) {
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