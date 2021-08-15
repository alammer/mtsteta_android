package ru.mtsteta.flixnet.fakeRepo

import ru.mtsteta.flixnet.movies.MovieDto

interface MoviesDataSource {
    fun getMovies(): List<MovieDto>?
}