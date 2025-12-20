package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.ResponseStatus
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.adapters.TracksAdapter

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var lastText = ""
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchQuery() }

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter

    private val trackInteractor = Creator.provideTrackInteractor()

    private lateinit var inputEditText: EditText
    private lateinit var historyTitle: TextView
    private lateinit var historyClearButton: Button
    private lateinit var backToMainButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var clearButton: ImageView
    private lateinit var noticeLayout: LinearLayout
    private lateinit var noticeImage: ImageView
    private lateinit var noticeText: TextView
    private lateinit var noticeRefreshButton: Button
    private lateinit var progressBar: ProgressBar

    private val tracksList: MutableList<Track> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        enableEdgeToEdge()
        setupEdgeToEdge()

        val clearSearchHistoryUseCase = Creator.provideClearSearchHistoryUseCase(this)
        val getSearchHistoryUseCase = Creator.provideGetSearchHistoryUseCase(this)
        val addTrackToSearchHistoryUseCase = Creator.provideAddTrackToSearchHistoryUseCase(this)

        var historyList = getSearchHistoryUseCase.execute()

        initViews()

        backToMainButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            tracksList.clear()
            noticeLayout.isVisible = false
            recyclerView.isVisible = false
            if (historyList.isNotEmpty()) {
                showHistory(true)
            }
        }

        val onTrackClick: (Track) -> Unit = { track ->
            if (clickDebounce()) {
                historyList = addTrackToSearchHistoryUseCase.execute(historyList, track)
                historyAdapter.notifyDataSetChanged()
                val intent = Intent(this, MediaPlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }

        tracksAdapter = TracksAdapter(tracksList, onTrackClick)
        historyAdapter = TracksAdapter(historyList, onTrackClick)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                recyclerView.adapter = historyAdapter
                if (historyList.isNotEmpty()) {
                    showHistory(true)
                }
            } else {
                historyTitle.isVisible = false
                historyClearButton.isVisible = false
            }
        }

        historyClearButton.setOnClickListener {
            clearSearchHistoryUseCase.execute()
            historyList = getSearchHistoryUseCase.execute()
            showHistory(false)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                recyclerView.adapter = historyAdapter
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastText = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                searchDebounce()

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    if (historyList.isNotEmpty()) {
                        showHistory(true)
                    }
                } else {
                    recyclerView.adapter = tracksAdapter
                    historyTitle.isVisible = false
                    historyClearButton.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
                true
            }
            false
        }

        noticeRefreshButton.setOnClickListener {
            searchQuery()
        }

    }

    private fun initViews() {
        linearLayout = findViewById(R.id.container)
        backToMainButton = findViewById(R.id.back_button)
        inputEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        noticeLayout = findViewById(R.id.notice_layout)
        noticeImage = findViewById(R.id.notice_image)
        noticeText = findViewById(R.id.notice_text)
        noticeRefreshButton = findViewById(R.id.refresh_button)
        historyTitle = findViewById(R.id.history_title)
        historyClearButton = findViewById(R.id.clear_history)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun showHistory(visibility: Boolean) {
        historyTitle.isVisible = visibility
        recyclerView.isVisible = visibility
        historyClearButton.isVisible = visibility
    }

    private var searchValue: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(PRODUCT_AMOUNT, "")
        inputEditText.setText(searchValue)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun searchQuery() {
        if (inputEditText.text.isNotEmpty()) {
            progressBar.isVisible = true
            trackInteractor.searchTracks(
                inputEditText.text.toString(),
                object : TrackInteractor.TrackConsumer {
                    override fun consume(foundTracks: List<Track>, status: ResponseStatus) {
                        runOnUiThread {
                            progressBar.isVisible = false
                            tracksList.clear()
                            when (status) {
                                ResponseStatus.SUCCESS -> {
                                    if (foundTracks.isNotEmpty()) {
                                        recyclerView.isVisible = true
                                        noticeLayout.isVisible = false
                                        tracksList.addAll(foundTracks)
                                        tracksAdapter.notifyDataSetChanged()
                                    } else {
                                        recyclerView.isVisible = false
                                        noticeLayout.isVisible = true
                                        noticeRefreshButton.isVisible = false
                                        noticeImage.setImageResource(R.drawable.ic_no_tracks_120)
                                        noticeText.setText(R.string.no_tracks)
                                    }
                                }

                                ResponseStatus.ERROR -> {
                                    recyclerView.isVisible = false
                                    noticeLayout.isVisible = true
                                    noticeRefreshButton.isVisible = true
                                    noticeImage.setImageResource(R.drawable.ic_internet_120)
                                    noticeText.setText(R.string.connection_error)
                                }
                            }
                        }
                    }

                })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun setupEdgeToEdge() {
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