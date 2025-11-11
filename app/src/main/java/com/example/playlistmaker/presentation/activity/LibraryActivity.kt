package com.example.playlistmaker.presentation.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        enableEdgeToEdge()

        setupEdgeToEdge()
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