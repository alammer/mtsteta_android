package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import ru.mtsteta.flixnet.database.Actor

@Keep
@Parcelize
data class ActorDto(
    val name: String,
    val bio: String,
    val imageUrl: String,
    val recentMovies: List<String>? = null
) : Parcelable

fun ActorDto.toDataBaseModel(): Actor = Actor(name, bio,  imageUrl, movieList = null)
