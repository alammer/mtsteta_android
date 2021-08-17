package ru.mtsteta.flixnet.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.mtsteta.flixnet.App


@Database(entities = [Movie::class, Actor::class, Genre::class], version = 2)
abstract class MovieDataBase: RoomDatabase() {

    abstract val movieDataDao: MovieDataDao

    companion object {
        private const val DB_NAME = "Movies.db"
        val instance: MovieDataBase  by lazy {
            Room.databaseBuilder(App.appContext, MovieDataBase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE actor_table RENAME COLUMN photo TO poster")
    }
}