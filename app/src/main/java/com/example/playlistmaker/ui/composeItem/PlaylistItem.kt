package com.example.playlistmaker.ui.composeItem

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.domain.models.Playlist


@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val imageUri = playlist.coverPath?.toUri()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    )
    {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(2.dp)),
            model = imageUri,
            placeholder = painterResource(R.drawable.ic_placeholder_312),
            error = painterResource(R.drawable.ic_placeholder_312),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .height(16.dp),
            text = playlist.playlistName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            modifier = Modifier.height(16.dp),
            text = formatCount(playlist.trackCount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

private fun formatCount(count: Int): String = when {
    count % 100 in 11..19 -> "$count треков"
    count % 10 == 1 -> "$count трек"
    count % 10 in 2..4 -> "$count трека"
    else -> "$count треков"
}

@Preview
@Composable
private fun PlaylistItemPreview() {
    PlaylistItem(
        Playlist(
            playlistId = 1,
            playlistName = "sadfgkl",
            playlistDescription = null,
            coverPath = null,
            trackCount = 5
        ), onClick = {}, onLongClick = {}

    )

}