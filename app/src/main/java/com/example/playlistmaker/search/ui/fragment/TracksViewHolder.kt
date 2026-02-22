package com.example.playlistmaker.search.ui.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackImage: ImageView = itemView.findViewById(R.id.track_image)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(model: Track) {
        Glide.with(itemView).load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner)))
            .into(trackImage)
        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())
    }

}