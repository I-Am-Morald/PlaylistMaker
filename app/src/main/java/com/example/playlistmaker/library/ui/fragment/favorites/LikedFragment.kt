package com.example.playlistmaker.library.ui.fragment.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentLikedBinding
import com.example.playlistmaker.library.ui.fragment.LibraryFragmentDirections
import com.example.playlistmaker.library.ui.view_model.LikedViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedFragment : Fragment() {

    private var _binding: FragmentLikedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LikedViewModel by viewModel()

    private var isClickAllowed = true

    private lateinit var favoritesAdapter: TracksAdapter

    private var favotiresList: MutableList<Track> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onTrackClick: (Track) -> Unit = { track ->
            if (isClickAllowed) {
                viewModel.clickDebounce()
                val action =
                    LibraryFragmentDirections.actionLibraryFragmentToMediaPlayerFragment(track)
                findNavController().navigate(action)
            }
        }

        favoritesAdapter = TracksAdapter(favotiresList, onTrackClick)

        binding.favoriteRecyclerView.adapter = favoritesAdapter

        viewModel.getFavorite().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.isClickAllowed.observe(viewLifecycleOwner) { isClickAllowed = it }

        viewModel.loadFavorites()
    }

    fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.NoData -> {
                binding.favoriteRecyclerView.isVisible = false
                binding.textPlaceholder.isVisible = true
                binding.imagePlaceholder.isVisible = true
            }

            is FavoriteState.FavoriteTracksList -> {
                binding.favoriteRecyclerView.isVisible = true
                binding.textPlaceholder.isVisible = false
                binding.imagePlaceholder.isVisible = false
                favotiresList.clear()
                favotiresList.addAll(state.data)
                favoritesAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = LikedFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}