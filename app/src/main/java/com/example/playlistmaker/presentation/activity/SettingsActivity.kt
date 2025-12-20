package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.presentation.App
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        enableEdgeToEdge()

        setupEdgeToEdge()

        val backToMainButton = findViewById<ImageView>(R.id.back_button)

        backToMainButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.shareButton)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton = findViewById<TextView>(R.id.supportButton)
        supportButton.setOnClickListener {
            supportApp()
        }

        val termsButton = findViewById<TextView>(R.id.termsButton)
        termsButton.setOnClickListener {
            termsApp()
        }

        val themeSwitcher = findViewById<Switch>(R.id.themesSwitcher)
        themeSwitcher.isChecked = (applicationContext as App).isDarkThemeEnabled()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.course_url))
        }
        startActivity(shareIntent)
    }

    private fun supportApp() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
        }
        startActivity(supportIntent)
    }

    private fun termsApp() {
        val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)))
        startActivity(termsIntent)
    }

    private fun setupEdgeToEdge() {
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