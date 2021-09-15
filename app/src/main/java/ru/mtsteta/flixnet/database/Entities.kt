package ru.mtsteta.flixnet.database

import androidx.room.*
import org.jetbrains.annotations.NotNull
import ru.mtsteta.flixnet.repo.ActorDto
import ru.mtsteta.flixnet.repo.MovieDto


@Entity(tableName = "movies")
data class MovieLocal(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    val id: Int? = 0,

    @NotNull
    @ColumnInfo(name = "movie_id")
    val movieId: Int,

    @NotNull
    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo
    val overview: String?,

    @ColumnInfo(name = "rating")
    val rateScore: Double? = 0.0,

    @ColumnInfo(name = "age_limit")
    var ageLimit: String?,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "backdrop_url")
    val backdropUrl: String?,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "genres")
    val genres: List<Int>?,
)

@Entity(tableName = "actors")
data class Actor(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
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



