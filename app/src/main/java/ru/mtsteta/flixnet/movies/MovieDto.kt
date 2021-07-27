package ru.mtsteta.flixnet.movies

data class MovieDto(
    val title: String,
    val description: String,
    val genre: String,
    val rateScore: Int,
    val ageRestriction: Int,
    val imageUrl: String
)
