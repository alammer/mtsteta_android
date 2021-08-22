package ru.mtsteta.flixnet.database

import androidx.room.*
import org.jetbrains.annotations.NotNull
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto

@Entity(tableName = "movies", primaryKeys = ["title", "poster"])
data class Movie(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    val movie_id: Int,

    @NotNull
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo
    var overview: String?,

    @ColumnInfo(name = "rating")
    var rateScore: Int = 0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: String?,

    @ColumnInfo(name = "poster")
    var imageUrl: String?,

    @ColumnInfo(name = "release_date")
    var release_date: String?,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "genres")
    var genres: List<Int>?,
)

@Entity(tableName = "actors")
data class Actor(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "photo")
    var imageUrl: String?,
)

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey
    @ColumnInfo(name = "genre")
    val genre: String
)

fun Movie.toDomainModel(): MovieDto = MovieDto(movie_id, title, overview, rateScore, ageLimit, imageUrl, release_date, genres)

fun Actor.toDomainModel(): ActorDto = ActorDto(name, imageUrl)



