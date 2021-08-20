package ru.mtsteta.flixnet.database

import androidx.room.*
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto

@Entity(tableName = "movies", primaryKeys = ["title", "poster"])
data class Movie(
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo
    var description: String,

    @ColumnInfo(name = "genre")
    var genre: String,

    @ColumnInfo(name = "rating")
    var rateScore: Int = 0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: Int = 18,

    @ColumnInfo(name = "poster")
    var imageUrl: String,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "actors")
    var actorsList: List<String>?
)

@Entity(tableName = "actors")
data class Actor(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "bio")
    var bio: String,

    @ColumnInfo(name = "photo")
    var imageUrl: String,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "movies")
    var movieList: List<String>?
)

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey
    @ColumnInfo(name = "genre")
    val genre: String
)

fun Movie.toDomainModel(): MovieDto = MovieDto(title, description,  genre, rateScore, ageLimit, imageUrl, topActors = null)

fun Actor.toDomainModel(): ActorDto = ActorDto(name, bio,  imageUrl, recentMovies = null)



