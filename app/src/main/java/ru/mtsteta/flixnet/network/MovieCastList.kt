package ru.mtsteta.flixnet.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieCastList(
    @SerialName("id")
    val movieId: Int,

    @SerialName("cast")
    val cast: List<MovieCrew>?
)

@Serializable
data class MovieCrew(
    @SerialName("known_for_department")
    val department: String?,

    @SerialName("name")
    val name: String?,

    @SerialName("profile_path")
    val imageUrl: String?,
)
