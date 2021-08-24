package ru.mtsteta.flixnet.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.mtsteta.flixnet.App


@Database(entities = [MovieLocal::class, Actor::class, GenreLocal::class], version = 1)
abstract class MovieDataBase: RoomDatabase() {

    abstract val movieDataDao: MovieDataDao

    companion object {
        private const val DB_NAME = "Movies.db"
        val instance: MovieDataBase  by lazy {
            Room.databaseBuilder(App.appContext, MovieDataBase::class.java, DB_NAME)
                .build()
        }
    }
}


