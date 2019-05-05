package com.arctouch.codechallenge.model

import com.squareup.moshi.Json
import java.io.Serializable

data class GenreResponse(val genres: List<Genre>)

data class Genre(val id: Int, val name: String) : Serializable

data class TrailerResponse(val id: Int, val results: List<Trailer>)

data class Trailer(val id: String, val key: String, val site: String)

data class UpcomingMoviesResponse(
    val page: Int,
    val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val genres: List<Genre>?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "vote_average") val voteAverage: String
) : Serializable
