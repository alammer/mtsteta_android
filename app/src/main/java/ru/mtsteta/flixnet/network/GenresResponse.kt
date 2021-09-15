package ru.mtsteta.flixnet.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mtsteta.flixnet.database.GenreLocal

@Serializable
data class GenresResponse(
    @SerialName("genres")
    val genreResponseList: List<GenreRemote>?,
)

@Serializable
data class GenreRemote(
    @SerialName("id")
    val genreId: Int,

    @SerialName("name")
    val genre: String,
)

fun GenreRemote.toDataBaseModel() = GenreLocal(genreId, genre)
