package ru.mtsteta.flixnet.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDataDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMovies(movies: List<Movie>)

    @Query("SELECT * from movie_table")
    fun getAllMovies(): List<Movie>?

    @Query("SELECT * from movie_table WHERE title = :title")
    fun getMovie(title: String) : Movie?

    @Query("DELETE FROM movie_table")
    fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllActors(actors: List<Actor>)

    @Query("SELECT * from actor_table")
    fun getAllActors(): List<Actor>?

    @Query("SELECT * from actor_table WHERE name = :name")
    fun getActor(name: String) : Actor?

    @Query("DELETE FROM actor_table")
    fun clearActors()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGenres(genres: List<String>)

    @Query("SELECT * from genre_table")
    fun getGenres(): List<String>?

    @Query("DELETE FROM genre_table")
    fun clearGenres()

}