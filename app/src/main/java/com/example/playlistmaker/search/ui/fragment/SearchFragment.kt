package com.example.playlistmaker.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private var isClickAllowed = true

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter

    private var historyList: MutableList<Track> = mutableListOf()
    private var tracksList: MutableList<Track> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.isClickAllowed.observe(viewLifecycleOwner) { isClickAllowed = it}

        val onTrackClick: (Track) -> Unit = { track ->
            if (isClickAllowed) {
                viewModel.clickDebounce()
                viewModel.addTrackToHistory(track)
                val action = SearchFragmentDirections.actionSearchFragmentToMediaPlayerFragment(track)
                findNavController().navigate(action)
            }
        }

        tracksAdapter = TracksAdapter(tracksList, onTrackClick)
        historyAdapter = TracksAdapter(historyList, onTrackClick)

        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                if (historyList.isNotEmpty()) {
                    showHistory(true)
                }
            } else {
                binding.historyTitle.isVisible = false
                binding.clearHistory.isVisible = false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.recyclerView.adapter = historyAdapter
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    viewModel.setSearchText(s.toString())
                }
                binding.clearIcon.isVisible = !s.isNullOrEmpty()

                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
                    if (historyList.isNotEmpty()) {
                        showHistory(true)
                    }
                } else {
                    viewModel.searchDebounce()
                    binding.recyclerView.adapter = tracksAdapter
                    binding.historyTitle.isVisible = false
                    binding.clearHistory.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
                true
            }
            false
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            viewModel.clearSearchQuery()
            hideKeyboard()
            tracksList.clear()
            binding.noticeLayout.isVisible = false
            binding.recyclerView.isVisible = false
            if (historyList.isNotEmpty()) {
                viewModel.loadSearchHistory()
            }
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            historyList.clear()
            showHistory(false)
        }

        binding.refreshButton.setOnClickListener {
            searchQuery()
        }

    }
    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError()
            is SearchState.NoResult -> showNoResult()
            is SearchState.SearchHistory -> {
                historyList.clear()
                historyList.addAll(state.data)
                if (historyList.isNotEmpty()) {
                    showHistory(true)
                } else {
                    showHistory(false)
                }
            }

            is SearchState.SearchHistoryUpdated -> {
                historyList.clear()
                historyList.addAll(state.data)
                historyAdapter.notifyDataSetChanged()
            }

            is SearchState.SearchTrackList -> {
                tracksList.clear()
                tracksList.addAll(state.data)
                tracksAdapter.notifyDataSetChanged()
                showResult()
            }
        }
    }

    fun searchQuery() {
        val query = binding.searchEditText.text
        if (query.isNotEmpty()) {
            viewModel.searchQuery(query.toString())
        }
    }

    private fun showHistory(visibility: Boolean) {
        binding.recyclerView.adapter = historyAdapter
        binding.progressBar.isVisible = false
        binding.historyTitle.isVisible = visibility
        binding.recyclerView.isVisible = visibility
        binding.clearHistory.isVisible = visibility
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun showResult() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = true
        binding.noticeLayout.isVisible = false
    }

    private fun showNoResult() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.noticeLayout.isVisible = true
        binding.refreshButton.isVisible = false
        binding.noticeImage.setImageResource(R.drawable.ic_no_tracks_120)
        binding.noticeText.setText(R.string.no_tracks)
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.noticeLayout.isVisible = true
        binding.refreshButton.isVisible = true
        binding.noticeImage.setImageResource(R.drawable.ic_internet_120)
        binding.noticeText.setText(R.string.connection_error)
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}