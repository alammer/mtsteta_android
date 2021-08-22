package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import ru.mtsteta.flixnet.database.Movie

@Keep
@Parcelize
data class MovieDto(
    val movie_id: Int,
    val title: String,
    val overview: String? = null,
    val rateScore: Int,
    val ageLimit: String? = null,
    val imageUrl: String? = null,
    val backdropUrl: String? = null,
    val release_date: String? = null,
    val genres: List<String>? = null,
) : Parcelable

fun MovieDto.toDataBaseModel(): Movie = Movie(movie_id, title, overview, rateScore, ageLimit, imageUrl, backdropUrl, release_date, genres)

