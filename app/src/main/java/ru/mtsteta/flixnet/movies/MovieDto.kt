package ru.mtsteta.flixnet.movies

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MovieDto(
    val title: String,
    val description: String,
    val genre: String,
    val rateScore: Int,
    val ageLimit: Int,
    val imageUrl: String
) : Parcelable
