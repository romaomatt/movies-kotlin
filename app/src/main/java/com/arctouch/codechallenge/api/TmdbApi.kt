package com.arctouch.codechallenge.api

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.MovieResponse
import com.arctouch.codechallenge.model.TrailerResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    fun genres(): Observable<GenreResponse>

    @GET("movie/{movie_id}/videos")
    fun getTrailer(
        @Path("movie_id") movieId: Int
    ): Single<TrailerResponse>

    @GET("movie/upcoming")
    fun upcomingMovies(
        @Query("page") page: Int
    ): Observable<MovieResponse>

    @GET("search/movie")
    fun searchMovie(
        @Query("query") query: String
    ): Observable<MovieResponse>

}
