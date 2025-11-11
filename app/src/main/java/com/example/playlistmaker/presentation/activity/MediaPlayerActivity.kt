package com.example.playlistmaker.presentation.activity

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    companion object {
        private const val UPDATE_INTERVAL = 500L
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

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

    private var previewUrl: String? = ""
    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val progressHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediaplayer)

        enableEdgeToEdge()

        setupEdgeToEdge()

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

            val artworkUrl = it.getCoverArtwork()
            previewUrl = it.previewUrl

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

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
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

    private fun progressTask() {
        if (playerState == PlayerState.PLAYING) {
            currentDuration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        }
    }

    private fun startProgressTask() {
        progressHandler.post(object : Runnable {
            override fun run() {
                progressTask()
                progressHandler.postDelayed(this, UPDATE_INTERVAL)
            }
        })
    }

    private fun stopProgressTask() {
        progressHandler.removeCallbacksAndMessages(null)
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }

            else -> Unit
        }
    }

    private fun preparePlayer() {
        if (previewUrl.isNullOrEmpty()) {
            //currentDuration.text = "N/A"  // Трек 'Rammstein - Du Hast : trackId = 1390562591' не дает URLa
            return
        }
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_play_button_100)
            currentDuration.text = "00:00"
            playerState = PlayerState.PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause_button_100)
        playerState = PlayerState.PLAYING
        startProgressTask()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play_button_100)
        playerState = PlayerState.PAUSED
        stopProgressTask()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopProgressTask()
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