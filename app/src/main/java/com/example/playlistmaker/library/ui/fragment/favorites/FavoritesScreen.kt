package com.example.playlistmaker.library.ui.fragment.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.ui.composeItem.TrackItem

@Composable
fun FavoritesScreen(favoritesViewModel: FavoritesViewModel, onTrackClick: (Track) -> Unit) {
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
    }
    when (val state = favoritesViewModel.favorite.collectAsState().value) {
        is FavoriteState.NoData -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 106.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_no_tracks_120),
                    contentDescription = null
                )
                Text(text = stringResource(R.string.no_liked_tracks),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        is FavoriteState.FavoriteTracksList -> {
            val trackList = state.data
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
    }
}