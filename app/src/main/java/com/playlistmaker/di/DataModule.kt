package com.playlistmaker.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.playlistmaker.data.db.AppDatabase
import com.playlistmaker.data.db.TrackDao
import com.playlistmaker.player.data.PlayerRepositoryImpl
import com.playlistmaker.player.domain.PlayerRepository
import com.playlistmaker.playlist.data.db.PlaylistDao
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.playlistmaker.sharing.domain.ExternalNavigator
import com.playlistmaker.util.AppConstants.ITUNES_BASE_URL
import com.playlistmaker.util.AppConstants.PREFS_NAME
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_tracks (
                trackId INTEGER NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTime TEXT NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT NOT NULL,
                releaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL,
                addedAt INTEGER NOT NULL,
                PRIMARY KEY(trackId)
            )
            """.trimIndent()
        )
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                coverPath TEXT NOT NULL,
                trackIds TEXT NOT NULL,
                trackCount INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
val dataModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .build()
    }

    single<ItunesApi> {
        get<Retrofit>().create(ItunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlistmaker.db"
        )
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    single<TrackDao> {
        get<AppDatabase>().trackDao()
    }

    single<PlaylistDao> {
        get<AppDatabase>().playlistDao()
    }

    single {
        get<AppDatabase>().playlistTrackDao()
    }
}