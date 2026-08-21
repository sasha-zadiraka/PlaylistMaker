package com.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.playlistmaker.playlist.data.db.PlaylistDao
import com.playlistmaker.playlist.data.db.PlaylistEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}