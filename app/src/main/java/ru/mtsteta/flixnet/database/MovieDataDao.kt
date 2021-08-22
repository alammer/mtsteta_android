package ru.mtsteta.flixnet.database

import androidx.room.*

@Dao
interface  MovieDataDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllMovies(movieLocals: List<MovieLocal>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): List<MovieLocal>?

    @Query("SELECT * FROM movies WHERE title = :title")
    fun getMovie(title: String) : MovieLocal?

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
    fun insertAllGenres(genreLocals: List<GenreLocal>)

    @Query("SELECT * FROM genres")
    fun getGenres(): List<GenreLocal>?

    @Query("DELETE FROM genres")
    fun clearGenres()

}