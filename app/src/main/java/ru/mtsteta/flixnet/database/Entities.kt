package ru.mtsteta.flixnet.database

import androidx.room.*

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    var movieId: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "genre")
    var genre: String,

    @ColumnInfo(name = "rating")
    var rateScore: Int = 0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: Int = 18,

    @ColumnInfo(name = "poster")
    var imageUrl: String,

    @ColumnInfo(name = "actors")
    @TypeConverters(Converters::class)
    var actorsList: List<String>? = null
)

@Entity(tableName = "genre_table")
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val genreId: Long = 0L,

    val genre: String
)



