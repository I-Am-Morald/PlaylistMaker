package com.example.playlistmaker.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var isClickAllowed = true
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            viewModel.isClickAllowed.observe(viewLifecycleOwner) { isClickAllowed = it}
            setContent {
                PlaylistMakerTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onTrackClick = { track ->
                            if (isClickAllowed) {
                                viewModel.clickDebounce()
                                viewModel.addTrackToHistory(track)
                                val action = SearchFragmentDirections.actionSearchFragmentToMediaPlayerFragment(track)
                                findNavController().navigate(action)
                            }
                        }
                    )
                }
            }
        }
    }
}