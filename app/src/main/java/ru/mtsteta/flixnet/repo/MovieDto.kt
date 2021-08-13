package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import ru.mtsteta.flixnet.database.Movie

@Keep
@Parcelize
data class MovieDto(
    val title: String,
    val description: String,
    val genre: String,
    val rateScore: Int,
    val ageLimit: Int,
    val imageUrl: String,
    val topActors: List<String>? = null
) : Parcelable

fun MovieDto.asDataBaseModel(): Movie = Movie(title, description,  genre, rateScore, ageLimit, imageUrl, actorsList = null)

