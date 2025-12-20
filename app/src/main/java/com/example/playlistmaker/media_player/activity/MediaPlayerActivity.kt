package com.example.playlistmaker.media_player.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaplayerBinding
import com.example.playlistmaker.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaplayerBinding

    private val viewModel: MediaPlayerViewModel by viewModels<MediaPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        viewModel.getState().observe(this) { state ->
            render(state)
        }

        getTrackFromIntent()

        binding.backButton.setOnClickListener {
            finish()
        }

        viewModel.preparePlayer()

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun render(state: MediaPlayerState) {
        when (state) {
            is MediaPlayerState.Prepared -> binding.playButton.isEnabled = true
            is MediaPlayerState.Playing -> binding.playButton.setImageResource(R.drawable.ic_pause_button_100)
            is MediaPlayerState.Paused -> binding.playButton.setImageResource(R.drawable.ic_play_button_100)
            is MediaPlayerState.Timer -> binding.currentDuration.text = state.data
            is MediaPlayerState.Complete -> {
                binding.playButton.setImageResource(R.drawable.ic_play_button_100)
                binding.currentDuration.text = getString(R.string.default_value)
            }

        }
    }

    private fun getTrackFromIntent() {
        @Suppress("DEPRECATION")
        val track = intent.getParcelableExtra<Track>("track")

        track?.let {
            binding.mediaTrackName.text = it.trackName
            binding.mediaArtistName.text = it.artistName
            val trackTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis.toLong())
            binding.durationValue.text = trackTime

            val artworkUrl = it.getCoverArtwork()
            viewModel.setPreviewUrl(it.previewUrl)

            Glide.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.ic_placeholder_312)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner)))
                .error(R.drawable.ic_placeholder_312)
                .into(binding.trackCover)

            if (!it.collectionName.isNullOrEmpty()) {
                binding.albumValue.text = it.collectionName
                binding.albumContainer.isVisible = true
            } else {
                binding.albumContainer.isVisible = false
            }

            val releaseYear = it.getFormattedDate()
            if (!releaseYear.isNullOrEmpty()) {
                binding.yearValue.text = releaseYear
                binding.yearContainer.isVisible = true
            } else {
                binding.yearContainer.isVisible = false
            }

            binding.genreValue.text = it.primaryGenreName ?: getString(R.string.unknown)
            binding.countryValue.text = it.country ?: getString(R.string.unknown)
        }
    }

    override fun onPause() {
        viewModel.mediaPlayerOnPaused()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.mediaPlayerOnDestroy()
        super.onDestroy()
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