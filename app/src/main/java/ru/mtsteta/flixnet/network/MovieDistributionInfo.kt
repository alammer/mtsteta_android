package ru.mtsteta.flixnet.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDistributionInfo(
    @SerialName("results")
    val results: List<CountryInfo>?
)

@Serializable
data class CountryInfo(
    @SerialName("iso_3166_1")
    val countryCode: String?,

    @SerialName("release_dates")
    val releaseDates: List<ReleaseDates>?
)

@Serializable
data class ReleaseDates(
    @SerialName("type")
    val type: Int,

    @SerialName("certification")
    val ageLimit: String?
)