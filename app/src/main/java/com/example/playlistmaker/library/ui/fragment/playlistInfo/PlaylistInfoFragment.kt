package com.example.playlistmaker.library.ui.fragment.playlistInfo

import android.os.Bundle
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
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.TracksAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistInfoFragmentArgs by navArgs()
    private val viewModel: PlaylistInfoViewModel by viewModel()

    private lateinit var currentPlaylist: Playlist
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var tracksAdapter: TracksAdapter
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var tracksList: MutableList<Track> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.playlist == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_not_found),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        viewModel.loadPlaylist(args.playlist.playlistId)

        viewModel.currentPlaylist.observe(viewLifecycleOwner) {
            currentPlaylist = it
            getInfoFromArguments()
        }

        viewModel.getTracksLists().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.trackDuration.text = duration
        }

        val onTrackClick: (Track) -> Unit = { track ->
            val action =
                PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToMediaPlayerFragment(track)
            findNavController().navigate(action)
        }

        tracksAdapter = TracksAdapter(tracksList, onTrackClick)

        tracksAdapter.onTrackClickLong = { track ->
            confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialog)
                .setMessage(R.string.delete_track_message)
                .setNeutralButton(R.string.delete_cancel) { dialog, which -> }
                .setPositiveButton(R.string.delete_confirm) { dialog, which ->
                    viewModel.deleteTrack(currentPlaylist, track)
                    /*             viewModel.refreshTracks(currentPlaylist.playlistId)*/
                }
            confirmDialog.show()
            true
        }

        binding.recyclerViewBS.adapter = tracksAdapter
        binding.recyclerViewBS.layoutManager = LinearLayoutManager(requireContext())

        initBottomSheets()
        buttonBinding()
    }

    private fun render(state: PlaylistInfoState) {
        when (state) {
            is PlaylistInfoState.NoData -> {
                tracksList.clear()
                tracksAdapter.notifyDataSetChanged()
                binding.emptyPlaceholder.isVisible = true
            }

            is PlaylistInfoState.PlaylistTrackList -> {
                binding.emptyPlaceholder.isVisible = false
                tracksList.clear()
                tracksList.addAll(state.data)
                tracksAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getInfoFromArguments() {
        binding.playlistName.text = currentPlaylist.playlistName
        binding.playlistDescription.text = currentPlaylist.playlistDescription
        binding.trackCount.text = formatCount(currentPlaylist.trackCount)
        Glide.with(this)
            .load(currentPlaylist.coverPath)
            .placeholder(R.drawable.ic_placeholder_312)
            .centerCrop()
            .into(binding.cover)
        viewModel.getPlaylistInfo(currentPlaylist.trackIds)
    }

    private fun formatCount(count: Int): String = when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }

    private fun initBottomSheets() {

        tracksBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistInfoBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }

        tracksBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = false
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        menuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistMenuBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
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

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun buttonBinding() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.playlistSettings.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.playlistNameBs.text = currentPlaylist.playlistName
            binding.trackCountBs.text = formatCount(currentPlaylist.trackCount)
            Glide.with(this).load(currentPlaylist.coverPath)
                .placeholder(R.drawable.ic_placeholder_312)
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_corner)))
                .into(binding.playlistCoverBs)

        }

        binding.playlistShare.setOnClickListener {
            sharePlaylist()
        }

        binding.menuShareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuEditButton.setOnClickListener {

            val action =
                PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToPlaylistCreateFragment(currentPlaylist)
            findNavController().navigate(action)
        }

        binding.menuDeleteButton.setOnClickListener {
            confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialog)
                .setMessage(
                    requireContext().getString(
                        R.string.delete_playlist_message,
                        currentPlaylist.playlistName
                    )
                )
                .setNeutralButton(R.string.delete_cancel) { dialog, which -> }
                .setPositiveButton(R.string.delete_confirm) { dialog, which ->
                    viewModel.deletePlaylist(currentPlaylist.playlistId)
                    findNavController().popBackStack()
                }
            confirmDialog.show()
        }
    }

    private fun sharePlaylist() {
        BottomSheetBehavior.from(binding.playlistMenuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (currentPlaylist.trackIds.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_empty),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sharePlaylist(currentPlaylist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}