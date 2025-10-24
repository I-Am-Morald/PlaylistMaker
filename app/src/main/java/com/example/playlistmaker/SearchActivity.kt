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
    private val tracksList: MutableList<Track> = mutableListOf()
    //val prefs = getSharedPreferences(SearchHistory.HISTORY_KEY, MODE_PRIVATE)
    //val historyList = SearchHistory(prefs).getHistory()
    //val historyList: MutableList<Track> = mutableListOf()
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

        val backToMainButton = findViewById<ImageView>(R.id.back_button)

        backToMainButton.setOnClickListener {
            finish()
        }

        val linearLayout = findViewById<LinearLayout>(R.id.container)
        val inputEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val noticeLayout = findViewById<LinearLayout>(R.id.notice_layout)
        val noticeImage = findViewById<ImageView>(R.id.notice_image)
        val noticeText = findViewById<TextView>(R.id.notice_text)
        val noticeRefreshButton = findViewById<Button>(R.id.refresh_button)
        val historyTitle = findViewById<TextView>(R.id.history_title)
        val historyClearButton = findViewById<Button>(R.id.clear_history)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            tracksList.clear()
            if(historyList.isEmpty()) {
                historyTitle.isVisible = false
                recyclerView.isVisible = false
                historyClearButton.isVisible = false
            }
            noticeLayout.isVisible = false
            historyTitle.isVisible = true
            historyClearButton.isVisible = true

        }

        val tracksAdapter = TracksAdapter(tracksList) { track ->
            // Обработка клика
            historyList = SearchHistory(prefs).addTrackToHistory(historyList, track)
        }
        //recyclerView.adapter = tracksAdapter
        val historyAdapter = TracksAdapter(historyList) { track ->
            historyList = SearchHistory(prefs).addTrackToHistory(historyList, track)
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                recyclerView.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
                if(historyList.isNotEmpty()) {
                    historyTitle.isVisible = true
                    recyclerView.isVisible = true
                    historyClearButton.isVisible = true
                }
            } else {
                historyTitle.isVisible = false
                historyClearButton.isVisible = false
            }
        }

        historyClearButton.setOnClickListener {
            SearchHistory(prefs).clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyTitle.isVisible = false
            recyclerView.isVisible = false
            historyClearButton.isVisible = false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                recyclerView.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastText = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    if(historyList.isNotEmpty()) {
                        historyTitle.isVisible = true
                        recyclerView.isVisible = true
                        historyClearButton.isVisible = true
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
                                noticeImage.setImageResource(R.drawable.ic_no_tracks_120)
                                noticeText.setText(R.string.no_tracks)
                                noticeRefreshButton.isVisible = false
                            } else {
                                recyclerView.isVisible = true
                                noticeLayout.isVisible = false
                            }
                        } else {
                            recyclerView.isVisible = false
                            noticeLayout.isVisible = true
                            noticeImage.setImageResource(R.drawable.ic_internet_120)
                            noticeText.setText(R.string.connection_error)
                            noticeRefreshButton.isVisible = true
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        recyclerView.isVisible = false
                        noticeLayout.isVisible = true
                        noticeImage.setImageResource(R.drawable.ic_internet_120)
                        noticeText.setText(R.string.connection_error)
                        noticeRefreshButton.isVisible = true
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

