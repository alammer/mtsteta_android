package ru.mtsteta.flixnet.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.mtsteta.flixnet.App


@Database(entities = [MovieLocal::class, PagingKeys::class, Actor::class, GenreLocal::class], version = 1, exportSchema = false)
abstract class MovieDataBase: RoomDatabase() {

    abstract val movieDataDao: MovieDataDao
    abstract val pagingKeysDao: PagingKeysDao

    companion object {
        private const val DB_NAME = "Movies.db"
        val instance: MovieDataBase  by lazy {
            Room.databaseBuilder(App.appContext, MovieDataBase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}


