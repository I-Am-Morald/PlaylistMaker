package com.example.playlistmaker.media_player.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
            Toast.makeText(requireContext(), getString(R.string.not_found), Toast.LENGTH_SHORT)
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

        viewModel.preparePlayer()
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
                    bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
                        state = BottomSheetBehavior.STATE_HIDDEN}
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
        when (mediaState) {
            is MediaPlayerState.Prepared -> binding.playButton.isEnabled = true
            is MediaPlayerState.Playing -> binding.playButton.setImageResource(R.drawable.ic_pause_button_100)
            is MediaPlayerState.Paused -> binding.playButton.setImageResource(R.drawable.ic_play_button_100)
            is MediaPlayerState.Timer -> binding.currentDuration.text = mediaState.data
            is MediaPlayerState.Complete -> {
                binding.playButton.setImageResource(R.drawable.ic_play_button_100)
                binding.currentDuration.text = getString(R.string.default_value)
            }
        }
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
        viewModel.setPreviewUrl(playedTrack.previewUrl)

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
                MediaPlayerFragmentDirections.actionMediaPlayerFragmentToPlaylistCreateFragment()
            findNavController().navigate(action)
        }

        binding.playButton.setOnClickListener {
                viewModel.playbackControl()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack() //navigateUp()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked(playedTrack)
        }
    }

    override fun onResume() {
        super.onResume()
        BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onPause() {
        viewModel.mediaPlayerOnPaused()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.releasePlayer()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}