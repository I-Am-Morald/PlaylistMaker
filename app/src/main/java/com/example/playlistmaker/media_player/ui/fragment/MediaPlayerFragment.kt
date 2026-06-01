package com.example.playlistmaker.media_player.ui.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaplayerBinding
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.playlist.PlaylistBottomSheetAdapter
import com.example.playlistmaker.library.ui.view_model.AddTrackState
import com.example.playlistmaker.media_player.service.MediaPlayerService
import com.example.playlistmaker.media_player.service.MediaPlayerState
import com.example.playlistmaker.media_player.ui.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class MediaPlayerFragment : Fragment() {

    private var _binding: FragmentMediaplayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<MediaPlayerViewModel>()

    private val args: MediaPlayerFragmentArgs by navArgs()

    private lateinit var playedTrack: Track
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistBSAdapter: PlaylistBottomSheetAdapter

    private var playlists: MutableList<Playlist> = mutableListOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindMediaPlayerService()
        } else {
            Toast.makeText(requireContext(), "Can't start foreground service!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerServiceBinder
            viewModel.setMediaPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeMediaPlayerControl()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.track == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.track_not_found),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        playedTrack = args.track

        val onPlaylistClick: (Playlist) -> Unit = { playlist ->
            viewModel.addTrackToPlaylist(playlist, playedTrack)
        }

        playlistBSAdapter = PlaylistBottomSheetAdapter(playlists, onPlaylistClick)
        binding.recyclerViewBS.adapter = playlistBSAdapter
        binding.recyclerViewBS.layoutManager = LinearLayoutManager(requireContext())

        observers()
        getTrackFromArguments()
        initBottomSheet()
        buttonBinding()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            bindMediaPlayerService()
        }
    }

    private fun observers() {
        viewModel.playlistsList.observe(viewLifecycleOwner) { playlistsList ->
            playlists.clear()
            playlists.addAll(playlistsList)
            playlistBSAdapter.notifyDataSetChanged()
        }

        viewModel.getMediaState().observe(viewLifecycleOwner) { mediaState ->
            render(mediaState)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            showIsFavorite(isFavorite)
        }

        viewModel.addTrackStatus.observe(viewLifecycleOwner) { status ->
            val message: String
            when (status) {
                is AddTrackState.Error -> {
                    message = getString(R.string.add_track_error)
                }

                is AddTrackState.Success -> {
                    message = getString(R.string.add_track_success) + " " + status.playlistName
                    bottomSheetBehavior =
                        BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
                            state = BottomSheetBehavior.STATE_HIDDEN
                        }
                }

                is AddTrackState.AlreadyExist -> {
                    message = getString(R.string.add_track_exist)
                }
            }
            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun render(mediaState: MediaPlayerState) {
        binding.playButton.isEnabled = mediaState.isPlayButtonEnabled
        binding.playButton.buttonState(mediaState.isButtonPaused)
        binding.currentDuration.text = mediaState.progress
    }

    private fun getTrackFromArguments() {

        binding.mediaTrackName.text = playedTrack.trackName
        binding.mediaArtistName.text = playedTrack.artistName
        val trackTime =
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(playedTrack.trackTimeMillis.toLong())
        binding.durationValue.text = trackTime

        val artworkUrl = playedTrack.getCoverArtwork()

        Glide.with(this)
            .load(artworkUrl)
            .placeholder(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.album_cover_corner)))
            .error(R.drawable.ic_placeholder_312)
            .into(binding.trackCover)

        if (!playedTrack.collectionName.isNullOrEmpty()) {
            binding.albumValue.text = playedTrack.collectionName
            binding.albumContainer.isVisible = true
        } else {
            binding.albumContainer.isVisible = false
        }

        val releaseYear = playedTrack.getFormattedDate()
        if (!releaseYear.isNullOrEmpty()) {
            binding.yearValue.text = releaseYear
            binding.yearContainer.isVisible = true
        } else {
            binding.yearContainer.isVisible = false
        }

        viewModel.setIsFavorite(playedTrack.trackId)

        binding.genreValue.text = playedTrack.primaryGenreName ?: getString(R.string.unknown)
        binding.countryValue.text = playedTrack.country ?: getString(R.string.unknown)
    }

    fun showIsFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            binding.likeButton.setImageResource(R.drawable.ic_like_button_on_51)
        } else {
            binding.likeButton.setImageResource(R.drawable.ic_like_button_off_51)
        }
    }

    fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun buttonBinding() {
        binding.addToAlbumButton.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.addPlaylistButton.setOnClickListener {
            val action =
                MediaPlayerFragmentDirections.actionMediaPlayerFragmentToPlaylistCreateFragment(null)
            findNavController().navigate(action)
        }

        binding.playButton.playbackClickListener = {
            viewModel.playbackControl()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack() //navigateUp()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked(playedTrack)
        }
    }

    private fun bindMediaPlayerService() {
        val intent = Intent(requireContext(), MediaPlayerService::class.java).apply {
            putExtra("preview_url", playedTrack.previewUrl)
            putExtra("artist_name", playedTrack.artistName)
            putExtra("track_name", playedTrack.trackName)
        }

        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMediaPlayerService() {
        requireContext().unbindService(serviceConnection)
    }

    override fun onResume() {
        super.onResume()
        BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        viewModel.stopForegroundNotification()
    }

    override fun onPause() {
        super.onPause()
        viewModel.startForegroundNotification()
    }

    override fun onDestroy() {
        unbindMediaPlayerService()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}