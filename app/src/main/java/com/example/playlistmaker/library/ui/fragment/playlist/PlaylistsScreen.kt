package com.example.playlistmaker.library.ui.fragment.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.composeItem.PlaylistItem

@Composable
fun PlaylistsScreen(
    playlistsViewModel: PlaylistsViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    onCreateButtonClick: () -> Unit
) {

    LaunchedEffect(Unit) {
        playlistsViewModel.loadPlaylists()
    }
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onCreateButtonClick,
            modifier = Modifier.padding(top = 24.dp),
            enabled = true,
            shape = RoundedCornerShape(54.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                style = MaterialTheme.typography.titleMedium
            )
        }
        when (val state = playlistsViewModel.playlists.collectAsState().value) {
            is PlaylistsState.NoData -> {
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
                    Text(text = stringResource(R.string.no_playlists),
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            is PlaylistsState.Playlists -> {
                val playlists = state.data
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(playlists.size) { item ->
                        val playlist = playlists[item]
                        PlaylistItem(
                            playlist = playlist, onClick = { onPlaylistClick(playlists[item]) },
                            onLongClick = { }
                        )
                    }

                }
            }
        }
    }
}
