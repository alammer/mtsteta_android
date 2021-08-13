package ru.mtsteta.flixnet.repo

interface MoviesDataSource {
    fun getMovies(): List<MovieDto>?

    fun getActors(): List<ActorDto>?
}