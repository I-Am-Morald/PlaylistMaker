package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.activity.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()

        setupEdgeToEdge()

        //клик на поиск

        val buttonSearch = findViewById<Button>(R.id.search_button)

        buttonSearch.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        //клик на медиатеку

        val buttonLibrary = findViewById<Button>(R.id.library_button)

        buttonLibrary.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        // Клик на настройки

        val buttonSettings = findViewById<Button>(R.id.settings_button)

        buttonSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
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