package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.toString

class SearchActivity : AppCompatActivity() {

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        const val ITUNES_SEARCH_URL = "https://itunes.apple.com"
    }

    private var lastText = ""

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

    private val tracksList: MutableList<Track> = mutableListOf()
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_SEARCH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val prefs = getSharedPreferences(SearchHistory.HISTORY_KEY, MODE_PRIVATE)
        var historyList = SearchHistory(prefs).getHistory()

        initViews()

        backToMainButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            tracksList.clear()
            noticeLayout.isVisible = false
            recyclerView.isVisible = false
            if (historyList.isNotEmpty()) {
                showHistory(true)
            }
        }

        val tracksAdapter = TracksAdapter(tracksList) { track ->
            historyList = SearchHistory(prefs).addTrackToHistory(historyList, track)
        }
        val historyAdapter = TracksAdapter(historyList) { track ->
            historyList = SearchHistory(prefs).addTrackToHistory(historyList, track)
        }

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
            SearchHistory(prefs).clearHistory()
            historyList = SearchHistory(prefs).getHistory()
            showHistory(false)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                recyclerView.adapter = historyAdapter
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastText = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()

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

        fun searchQuery() {
            if (inputEditText.text.isNotEmpty()) {
                iTunesService.search(inputEditText.text.toString()).enqueue(object :
                    Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.isSuccessful()) {
                            tracksList.clear()
                            val results = response.body()?.results ?: emptyList()
                            if (results.isNotEmpty()) {
                                tracksList.addAll(results)
                                tracksAdapter.notifyDataSetChanged()
                            }
                            if (tracksList.isEmpty()) {
                                recyclerView.isVisible = false
                                noticeLayout.isVisible = true
                                noticeRefreshButton.isVisible = false
                                noticeImage.setImageResource(R.drawable.ic_no_tracks_120)
                                noticeText.setText(R.string.no_tracks)
                            } else {
                                recyclerView.isVisible = true
                                noticeLayout.isVisible = false
                            }
                        } else {
                            recyclerView.isVisible = false
                            noticeLayout.isVisible = true
                            noticeRefreshButton.isVisible = true
                            noticeImage.setImageResource(R.drawable.ic_internet_120)
                            noticeText.setText(R.string.connection_error)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        recyclerView.isVisible = false
                        noticeLayout.isVisible = true
                        noticeRefreshButton.isVisible = true
                        noticeImage.setImageResource(R.drawable.ic_internet_120)
                        noticeText.setText(R.string.connection_error)
                    }

                })
            }
        }

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
}

