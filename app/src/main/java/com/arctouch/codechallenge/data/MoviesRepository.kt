package com.arctouch.codechallenge.data

import com.arctouch.codechallenge.api.TmdbApi

class MoviesRepository(private val api: TmdbApi) {

    fun getGenres() = api.genres()

    fun getTrailer(movieId: Int) = api.getTrailer(movieId)

    fun getUpcomingMovies(page: Int) = api.upcomingMovies(page)

}