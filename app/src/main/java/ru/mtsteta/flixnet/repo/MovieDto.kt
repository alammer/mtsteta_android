package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import ru.mtsteta.flixnet.database.MovieLocal

@Keep
@Parcelize
data class MovieDto(
    val movie_id: Int,
    val title: String,
    val overview: String? = null,
    val rateScore: Double,
    val ageLimit: String? = null,
    val imageUrl: String? = null,
    val backdropUrl: String? = null,
    val release_date: String? = null,
    val genres: List<Int>? = null,
) : Parcelable


