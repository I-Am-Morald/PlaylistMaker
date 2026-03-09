package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.data.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity): Int

    @Query("SELECT * FROM playlists ORDER BY playlistId DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlists WHERE  playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long) : PlaylistEntity

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long): Int

    @Query("SELECT trackCount FROM playlists WHERE playlistId = :playlistId")
    suspend fun getTrackCountById(playlistId: Long): Int?
}