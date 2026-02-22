package com.example.playlistmaker.media_player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaplayerBinding
import com.example.playlistmaker.media_player.ui.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
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
            Toast.makeText(requireContext(), "Трек не найден", Toast.LENGTH_SHORT).show()
            return
        }

        playedTrack = args.track

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        getTrackFromArguments()

        viewModel.preparePlayer()

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack() //navigateUp()
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

    private fun getTrackFromArguments() {

            binding.mediaTrackName.text = playedTrack.trackName
            binding.mediaArtistName.text = playedTrack.artistName
            val trackTime =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(playedTrack.trackTimeMillis.toLong())
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

            binding.genreValue.text = playedTrack.primaryGenreName ?: getString(R.string.unknown)
            binding.countryValue.text = playedTrack.country ?: getString(R.string.unknown)
    }

    override fun onPause() {
        viewModel.mediaPlayerOnPaused()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}