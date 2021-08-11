package ru.mtsteta.flixnet.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MovieDataBaseDao{

    @Insert
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Query("SELECT * from movie_table WHERE title = :title")
    fun getMovie(title: String) : Movie?

    @Query("DELETE FROM movie_table")
    suspend fun clear()

}