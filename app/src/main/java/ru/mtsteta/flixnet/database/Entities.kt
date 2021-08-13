package ru.mtsteta.flixnet.database

import androidx.room.*
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto

@Entity(tableName = "movie_table", primaryKeys = ["title", "actors"])
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

    @ColumnInfo(name = "actors")
    @TypeConverters(Converters::class)
    var actorsList: List<String>? = null
)

@Entity(tableName = "actor_table")
data class Actor(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "bio")
    var bio: String,

    @ColumnInfo(name = "photo")
    var imageUrl: String,

    @ColumnInfo(name = "movies")
    @TypeConverters(Converters::class)
    var movieList: List<String>? = null
)

@Entity(tableName = "genre_table")
data class Genre(
    @PrimaryKey
    val genre: String
)

fun Movie.asDomainModel(): MovieDto = MovieDto(title, description,  genre, rateScore, ageLimit, imageUrl, topActors = null)

fun Actor.asDomainModel(): ActorDto = ActorDto(name, bio,  imageUrl, recentMovies = null)



