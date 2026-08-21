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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        migrateTrackTimeColumn(db, tableName = "favorite_tracks")
        migrateTrackTimeColumn(db, tableName = "playlist_tracks")
    }

    private fun migrateTrackTimeColumn(
        db: SupportSQLiteDatabase,
        tableName: String
    ) {
        val newTableName = "${tableName}_new"

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $newTableName (
                trackId INTEGER NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
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

        val trackTimeMillisSelector = if (hasColumn(db, tableName, "trackTime")) {
            """
            (CAST(substr(trackTime, 1, instr(trackTime, ':') - 1) AS INTEGER) * 60 +
             CAST(substr(trackTime, instr(trackTime, ':') + 1) AS INTEGER)) * 1000
            """.trimIndent()
        } else {
            "0"
        }

        db.execSQL(
            """
            INSERT INTO $newTableName
            SELECT trackId, trackName, artistName, $trackTimeMillisSelector,
                artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl, addedAt
            FROM $tableName
            """.trimIndent()
        )

        db.execSQL("DROP TABLE $tableName")
        db.execSQL("ALTER TABLE $newTableName RENAME TO $tableName")
    }

    private fun hasColumn(
        db: SupportSQLiteDatabase,
        tableName: String,
        columnName: String
    ): Boolean {
        db.query("PRAGMA table_info($tableName)").use { cursor ->
            val nameColumnIndex = cursor.getColumnIndex("name")

            while (cursor.moveToNext()) {
                if (cursor.getString(nameColumnIndex) == columnName) {
                    return true
                }
            }
        }

        return false
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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