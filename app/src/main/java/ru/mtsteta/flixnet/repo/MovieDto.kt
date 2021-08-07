package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDto(
    val title: String? = null,
    val description: String,
    val genre: String,
    val rateScore: Int,
    val ageLimit: Int,
    val imageUrl: String
) : Parcelable
