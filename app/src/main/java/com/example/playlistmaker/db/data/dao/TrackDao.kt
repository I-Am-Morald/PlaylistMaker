package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favorite_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: String): Int

    @Query("SELECT * FROM favorite_tracks")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getTracksIdList(): List<String>

}