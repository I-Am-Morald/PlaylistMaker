package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.ui.fragment.SearchFragmentDirections
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LibraryFragment : Fragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            favoritesViewModel.isClickAllowed.observe(viewLifecycleOwner) { isClickAllowed = it }
            setContent {
                PlaylistMakerTheme {
                    LibraryScreen(
                        favoritesViewModel,
                        playlistsViewModel,
                        onTrackClick = { track ->
                            if (isClickAllowed) {
                                favoritesViewModel.clickDebounce()
                                val action =
                                    LibraryFragmentDirections.actionLibraryFragmentToMediaPlayerFragment(
                                        track
                                    )
                                findNavController().navigate(action)
                            }
                        },
                        onPlaylistClick = { playlist ->
                            if (isClickAllowed) {
                                playlistsViewModel.clickDebounce()
                                val action = LibraryFragmentDirections.actionLibraryFragmentToPlaylistInfoFragment(playlist)
                                findNavController().navigate(action)
                            }
                        },
                        onCreateButtonClick = {
                            val action =
                                LibraryFragmentDirections.actionLibraryFragmentToPlaylistCreateFragment(
                                    null
                                )
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}