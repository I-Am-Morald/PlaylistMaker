package com.example.playlistmaker.library.ui.fragment.playlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.domain.models.Playlist

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val trackCount: TextView = itemView.findViewById(R.id.track_count)
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    fun bind(model: Playlist) {
        playlistName.text = model.playlistName
        trackCount.text = formatCount(model.trackCount)
        Glide.with(itemView).load(model.coverPath)
            .placeholder(R.drawable.ic_placeholder_312)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner)))
            .into(playlistCover)
    }

    private fun formatCount(count: Int): String = when {
        count % 100 in 11..19 -> "$count треков"
        count % 10 == 1 -> "$count трек"
        count % 10 in 2..4 -> "$count трека"
        else -> "$count треков"
    }
}