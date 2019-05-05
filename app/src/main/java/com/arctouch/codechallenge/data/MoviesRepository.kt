package com.arctouch.codechallenge.data

import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.util.DEFAULT_REGION

class MoviesRepository(private val api: TmdbApi) {

    fun getGenres() = api.genres()
    fun getUpcomingMovies(page: Long) = api.upcomingMovies(page, DEFAULT_REGION)
    fun getMovies() = api.discoverMovies()
    fun getTrailer(movieId: Int) = api.getTrailer(movieId)

}