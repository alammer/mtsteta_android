package ru.mtsteta.flixnet.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDataDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): List<Movie>?

    @Query("SELECT * FROM movies WHERE title = :title")
    fun getMovie(title: String) : Movie?

    @Query("DELETE FROM movies")
    fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllActors(actors: List<Actor>)

    @Query("SELECT * FROM actors")
    fun getAllActors(): List<Actor>?

    @Query("SELECT * FROM actors WHERE name = :name")
    fun getActor(name: String) : Actor?

    @Query("DELETE FROM actors")
    fun clearActors()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenres(genres: List<Genre>)

    @Query("SELECT * FROM genres")
    fun getGenres(): List<String>?

    @Query("DELETE FROM genres")
    fun clearGenres()

}