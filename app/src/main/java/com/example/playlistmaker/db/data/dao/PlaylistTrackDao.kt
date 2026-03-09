package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: String): PlaylistTrackEntity

    @Query("SELECT * FROM playlist_tracks WHERE trackId in (:trackIds) ORDER BY createdAt")
    suspend fun getTracksListById(trackIds: List<String>): List<PlaylistTrackEntity>

    @Query("SELECT trackTimeMillis FROM playlist_tracks WHERE trackId in (:trackIds)")
    suspend fun getTracksDuration(trackIds: List<String>): List<Long>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: String): Int

}