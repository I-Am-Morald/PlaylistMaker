package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.media_player.activity.MediaPlayerActivity
import com.example.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.factory(this)
    }

    private var lastText = ""
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery() }

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var binding: ActivitySearchBinding

    private var historyList: MutableList<Track> = mutableListOf()
    private var tracksList: MutableList<Track> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        viewModel.getState().observe(this) { state ->
            render(state)
        }

        val onTrackClick: (Track) -> Unit = { track ->
            if (clickDebounce()) {
                viewModel.addTrackToHistory(track)
                val intent = Intent(this, MediaPlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
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
                lastText = s.toString()
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                searchDebounce()

                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
                    if (historyList.isNotEmpty()) {
                        showHistory(true)
                    }
                } else {
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

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
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

    private var searchValue: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(PRODUCT_AMOUNT, "")
        binding.searchEditText.setText(searchValue)
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )

            insets
        }
    }
}