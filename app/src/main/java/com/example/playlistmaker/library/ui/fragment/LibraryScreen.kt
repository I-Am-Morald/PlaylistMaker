package com.example.playlistmaker.library.ui.fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.domain.models.Playlist
import com.example.playlistmaker.library.ui.fragment.favorites.FavoritesScreen
import com.example.playlistmaker.library.ui.fragment.playlist.PlaylistsScreen
import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.ui.composeItem.ScreenTitle
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    favoritesViewModel: FavoritesViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreateButtonClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(R.string.liked_tracks, R.string.playlists)
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScreenTitle(stringResource(R.string.library))
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> FavoritesScreen(favoritesViewModel, onTrackClick)
                1 -> PlaylistsScreen(playlistsViewModel, onPlaylistClick, onCreateButtonClick)
            }
        }
    }
}