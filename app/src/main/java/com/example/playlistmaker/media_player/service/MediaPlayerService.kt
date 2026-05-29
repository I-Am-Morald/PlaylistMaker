package com.example.playlistmaker.media_player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.media_player.domain.MediaPlayerControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerService : Service(), MediaPlayerControl {

    inner class MediaPlayerServiceBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    private val _mediaPlayerState = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Default())
    val mediaPlayerState = _mediaPlayerState.asStateFlow()

    private val binder = MediaPlayerServiceBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var previewUrl = ""
    private var artistName = ""
    private var trackName = ""

    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        previewUrl = intent?.getStringExtra("preview_url") ?: ""
        artistName = intent?.getStringExtra("artist_name") ?: "Unknown artist"
        trackName = intent?.getStringExtra("track_name") ?: "Unknown track"
        preparePlayer()
        return binder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun getPlayerState(): StateFlow<MediaPlayerState> = mediaPlayerState

    private fun preparePlayer() {
        if (previewUrl.isEmpty()) return
        mediaPlayer?.setDataSource(previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _mediaPlayerState.value = MediaPlayerState.Prepared()
        }
        mediaPlayer?.setOnCompletionListener {
            _mediaPlayerState.value = MediaPlayerState.Prepared()
            timerJob?.cancel()
            stopForegroundNotification()
        }
    }

    override fun playbackControl() {
        if (mediaPlayer?.isPlaying == true)
            pausePlayer()
        else
            startPlayer()
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _mediaPlayerState.value = MediaPlayerState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        timerJob?.cancel()
        mediaPlayer?.pause()
        _mediaPlayerState.value = MediaPlayerState.Paused(getCurrentPlayerPosition())
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(UPDATE_INTERVAL)
                _mediaPlayerState.value = MediaPlayerState.Playing(getCurrentPlayerPosition())
            }
            _mediaPlayerState.value = MediaPlayerState.Prepared()
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }

    fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        stopForegroundNotification()
    }


    override fun startForegroundNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    override fun stopForegroundNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackName")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSilent(true)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private companion object {
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
        private const val UPDATE_INTERVAL = 300L
        const val SERVICE_NOTIFICATION_ID = 100
    }
}