package ru.mtsteta.flixnet.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mtsteta.flixnet.database.MovieLocal

@Serializable
data class PopMoviesResponse(
    @SerialName("page")
    val popListPage: Int,

    @SerialName("results")
    val responseMoviesList: List<MovieRemote> = emptyList(),
)

@Serializable
data class MovieRemote(
    @SerialName("id")
    val movieId: Int,

    @SerialName("genre_ids")
    val genresIds: List<Int>?,

    @SerialName("overview")
    val overview: String?,

    @SerialName("poster_path")
    val imageUrl: String?,

    @SerialName("backdrop_path")
    val backdropUrl: String?,

    @SerialName("release_date")
    val releaseDate: String?,

    @SerialName("title")
    val title: String?,

    @SerialName("vote_average")
    val rateScore: Double?,
)

fun MovieRemote.toDataBaseModel() = MovieLocal(id = null, movieId, title, overview, rateScore, ageLimit = null, imageUrl, backdropUrl, releaseDate, genresIds)
