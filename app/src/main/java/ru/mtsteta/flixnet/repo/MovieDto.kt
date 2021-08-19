package ru.mtsteta.flixnet.repo

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MovieDto(
    val title: String?,
    val description: String,
    val genre: String,
    val rateScore: Int,
    val ageLimit: Int,
    val imageUrl: String
) : Parcelable
