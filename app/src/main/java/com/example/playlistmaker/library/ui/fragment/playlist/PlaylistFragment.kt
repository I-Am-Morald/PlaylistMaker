package com.example.playlistmaker.library.ui.fragment.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.LibraryFragmentDirections
import com.example.playlistmaker.library.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var playlistAdapter: PlaylistAdapter

    private var isClickAllowed = true
    private var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.isClickAllowed.observe(viewLifecycleOwner) { isClickAllowed = it}

        val onPlaylistClick: (Playlist) -> Unit = { playlist ->
            if (isClickAllowed) {
                            viewModel.clickDebounce()
                            val action = LibraryFragmentDirections.actionLibraryFragmentToPlaylistInfoFragment(playlist)
                            findNavController().navigate(action)
                        }
        }

        playlistAdapter = PlaylistAdapter(playlists, onPlaylistClick)

        binding.recyclerView.adapter = playlistAdapter

        binding.createPlaylistButton.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToPlaylistCreateFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.NoData -> {
                binding.recyclerView.isVisible = false
                binding.textPlaceholder.isVisible = true
                binding.imagePlaceholder.isVisible = true
            }

            is PlaylistsState.Playlists -> {
                binding.recyclerView.isVisible = true
                binding.textPlaceholder.isVisible = false
                binding.imagePlaceholder.isVisible = false
                playlists.clear()
                playlists.addAll(state.data)
                playlistAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    override fun onResume() {
        viewModel.refreshPlaylists()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}