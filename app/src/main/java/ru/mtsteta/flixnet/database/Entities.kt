package ru.mtsteta.flixnet.database

import androidx.room.*
import org.jetbrains.annotations.NotNull
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto


@Entity(tableName = "movies")
data class MovieLocal(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    val movieId: Int,

    @NotNull
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo
    var overview: String?,

    @ColumnInfo(name = "rating")
    var rateScore: Double = 0.0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: String?,

    @ColumnInfo(name = "poster")
    var imageUrl: String?,

    @ColumnInfo(name = "backdrop")
    var backdropUrl: String?,

    @ColumnInfo(name = "release_date")
    var releaseDate: String?,

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
data class GenreLocal(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "genre_id")
    val genreId: Int,

    @ColumnInfo(name = "genre")
    val genre: String
)

fun MovieLocal.toDomainModel(): MovieDto = MovieDto(movieId, title, overview, rateScore, ageLimit, imageUrl, backdropUrl, releaseDate, genres)

fun Actor.toDomainModel(): ActorDto = ActorDto(name, imageUrl)



