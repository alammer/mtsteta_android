package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import ru.mtsteta.flixnet.database.Actor

@Keep
@Parcelize
data class ActorDto(
    val name: String,
    val imageUrl: String?,
) : Parcelable

fun ActorDto.toDataBaseModel(): Actor = Actor(name, imageUrl)
