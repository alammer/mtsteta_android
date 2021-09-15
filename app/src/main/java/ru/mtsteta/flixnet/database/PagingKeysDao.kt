package ru.mtsteta.flixnet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PagingKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PagingKeys>)

    @Query("SELECT * FROM pagingkeys WHERE movie_id = :id")
    suspend fun remoteKeysMovieId(id: Int): PagingKeys?

    @Query("DELETE FROM pagingkeys")
    suspend fun clearPagingKeys()
}