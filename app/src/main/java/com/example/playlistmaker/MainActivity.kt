package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}