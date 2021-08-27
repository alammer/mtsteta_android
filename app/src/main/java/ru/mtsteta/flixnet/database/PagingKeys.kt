package ru.mtsteta.flixnet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PagingKeys(@PrimaryKey val movieId: Int, val prevKey: Int?, val nextKey: Int?)
