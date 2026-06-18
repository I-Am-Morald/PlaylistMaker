package com.example.playlistmaker.ui.composeItem

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(start = 13.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            model = track.artworkUrl100,
            placeholder = painterResource(R.drawable.ic_placeholder_45),
            error = painterResource(R.drawable.ic_placeholder_45),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Row(
                modifier = Modifier
                    .height(13.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = track.artistName,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Icon(
                    painter = painterResource(R.drawable.dot),
                    modifier = Modifier.padding(all = 5.dp), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis),
                    modifier = Modifier.wrapContentWidth(),
                    fontSize = 11.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        Icon(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp),
            painter = painterResource(R.drawable.ic_arrow_forward_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

}

@Preview(name = "Portrait")
@Composable
private fun TrackElementPreview() {
    PlaylistMakerTheme {
        TrackItem(
            Track(
                trackId = "2",
                previewUrl = null,
                trackName = "Ghbsdfff",
                artistName = "Cuco askjdhfaskjdhfsakjdhf1234567890abcdef!!!1234567890abcdef",
                artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/98/3f/3a/983f3a97-a9ba-e291-9e7b-242e7c00c6ca/191061742319_1.jpg/100x100bb.jpg",
                country = "US",
                collectionName = "dfsdf",
                primaryGenreName = "rap",
                releaseDate = null,
                isFavorite = false,
                trackTimeMillis = 123124423,
            ),
            onLongClick = {},
            onClick = {}
        )
    }
}