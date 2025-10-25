package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var trackCover: ImageView
    private lateinit var mediaTrackName: TextView
    private lateinit var mediaArtistName: TextView
    private lateinit var addButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var likeButton: ImageView
    private lateinit var currentDuration: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumContainer: LinearLayout
    private lateinit var albumValue: TextView
    private lateinit var yearContainer: LinearLayout
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer)

        @Suppress("DEPRECATION")
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            intent.getSerializableExtra("track") as? Track
        }

        initViews()

        backButton.setOnClickListener {
            finish()
        }

        track?.let {
            mediaTrackName.text = it.trackName
            mediaArtistName.text = it.artistName
            val trackTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis.toLong())
            durationValue.text = trackTime
            currentDuration.text = trackTime

            val artworkUrl = it.getCoverArtwork()

            Glide.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.ic_placeholder_312)
                .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner)))
                .error(R.drawable.ic_placeholder_312)
                .into(trackCover)

            if (!it.collectionName.isNullOrEmpty()) {
                albumValue.text = it.collectionName
                albumContainer.isVisible = true
            } else {
                albumContainer.isVisible = false
            }

            val releaseYear = it.getFormattedDate()
            if (!releaseYear.isNullOrEmpty()) {
                yearValue.text = releaseYear
                yearContainer.isVisible = true
            } else {
                yearContainer.isVisible = false
            }

            genreValue.text = it.primaryGenreName ?: getString(R.string.unknown)
            countryValue.text = it.country ?: getString(R.string.unknown)
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_button)
        trackCover = findViewById(R.id.track_cover)
        mediaTrackName = findViewById(R.id.media_track_name)
        mediaArtistName = findViewById(R.id.media_artist_name)
        addButton = findViewById(R.id.add_to_album_button)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.like_button)
        currentDuration = findViewById(R.id.current_duration)
        durationValue = findViewById(R.id.duration_value)
        albumContainer = findViewById(R.id.album_container)
        albumValue = findViewById(R.id.album_value)
        yearContainer = findViewById(R.id.year_container)
        yearValue = findViewById(R.id.year_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
    }
}