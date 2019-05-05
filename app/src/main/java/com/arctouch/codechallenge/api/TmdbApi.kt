package com.arctouch.codechallenge.api

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    fun genres(): Observable<GenreResponse>

    @GET("discover/movie")
    fun discoverMovies(): Observable<UpcomingMoviesResponse>

    @GET("movie/upcoming")
    fun upcomingMovies(
        @Query("page") page: Long,
        @Query("region") region: String
    ): Observable<UpcomingMoviesResponse>

    @GET("movie/{id}")
    fun movie(
        @Path("id") id: Long
    ): Observable<Movie>

}
