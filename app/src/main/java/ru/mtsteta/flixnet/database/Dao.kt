package ru.mtsteta.flixnet.database

import androidx.room.*

@Dao
interface MovieDataBaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovie(movie: Movie)

    @Query("SELECT * from movie_table")
    suspend fun getAllMovies(): List<Movie>?

    @Query("SELECT * from movie_table WHERE title = :title")
    fun getMovie(title: String) : Movie?

    @Query("DELETE FROM movie_table")
    suspend fun clearMovies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre)

    @Query("SELECT * from genre_table")
    suspend fun getGenres(): List<Genre>?

    @Query("DELETE FROM genre_table")
    suspend fun clearGenres()

}