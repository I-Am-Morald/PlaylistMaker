package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(this)
    }

    private lateinit var binding: ActivitySettingsBinding

    private var switcherValue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge()

        with(binding) {
            backButton.setOnClickListener {
                finish()
            }

            shareButton.setOnClickListener {
                viewModel.shareApp()
            }

            supportButton.setOnClickListener {
                viewModel.supportApp()
            }

            termsButton.setOnClickListener {
                viewModel.termsApp()
            }
        }

        viewModel.darkTheme.observe(this) { isDark ->
            if (binding.themesSwitcher.isChecked != isDark) {
                binding.themesSwitcher.isChecked = isDark
            }
        }

        binding.themesSwitcher.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchTheme(isChecked)
        }
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )
            insets
        }
    }

}