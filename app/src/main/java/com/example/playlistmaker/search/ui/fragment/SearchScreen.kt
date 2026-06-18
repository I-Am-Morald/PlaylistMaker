package com.example.playlistmaker.search.ui.fragment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.ui.composeItem.ScreenTitle
import com.example.playlistmaker.ui.composeItem.TrackItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadSearchHistory()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle(stringResource(R.string.search))
        SearchInputField(viewModel)
        when (state) {
            is SearchState.Error -> ShowError(viewModel)
            is SearchState.Loading -> ShowLoading()
            is SearchState.NoResult -> ShowNoResult()
            is SearchState.SearchHistory -> ShowSearchHistory(viewModel, state.data, onTrackClick)
            is SearchState.SearchHistoryUpdated -> {}
            is SearchState.SearchTrackList -> ShowSearchTrackList(state.data, onTrackClick)
        }
    }
}

@Composable
private fun SearchInputField(viewModel: SearchViewModel) {
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = inputText,
        onValueChange = {
            inputText = it
            if (it.isEmpty()) {
                viewModel.loadSearchHistory()
            } else {
                viewModel.searchDebounce(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = Color.Black,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.searchQuery(inputText)
            }
        ),
        cursorBrush = SolidColor(Color.Black),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_16),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Поиск",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                    innerTextField()
                }
                if (inputText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_clear_icon_16),
                        contentDescription = "Очистить",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                inputText = ""
                                viewModel.loadSearchHistory()
                                keyboardController?.hide()
                            },
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    )
}

@Composable
private fun ShowError(viewModel: SearchViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_internet_120),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.connection_error),
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Button(
            modifier = Modifier
                .padding(top = 6.dp),
            shape = RoundedCornerShape(54.dp),
            onClick = { viewModel.refreshQuery() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.refresh),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

}

@Composable
private fun ShowLoading() {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(top = 140.dp)
            .size(44.dp),
        color = colorResource(R.color.YP_blue),
        strokeWidth = 4.dp
    )
}

@Composable
private fun ShowNoResult() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_no_tracks_120),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.no_tracks),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }

}

@Composable
private fun ShowSearchHistory(
    viewModel: SearchViewModel,
    historyList: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    if (historyList.isNotEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = stringResource(R.string.history_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(historyList.size) { item ->
                TrackItem(
                    track = historyList[item], onClick = {
                        onTrackClick(historyList[item])
                    },
                    onLongClick = { }
                )
            }

        }
        Button(
            modifier = Modifier
                .padding(top = 6.dp),
            shape = RoundedCornerShape(54.dp),
            onClick = { viewModel.clearHistory() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.history_clear),
                style = MaterialTheme.typography.titleMedium
            )

        }
    }
}

@Composable
private fun ShowSearchTrackList(trackList: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(trackList.size) { item ->
            val track = trackList[item]
            TrackItem(
                track = track, onClick = { onTrackClick(trackList[item]) },
                onLongClick = { }
            )
        }

    }
}