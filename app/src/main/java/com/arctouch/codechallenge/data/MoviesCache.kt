package com.arctouch.codechallenge.data

import com.arctouch.codechallenge.model.Genre

object MoviesCache {

    var genres = listOf<Genre>()

    fun cacheGenres(genres: List<Genre>) {
        MoviesCache.genres = genres
    }
}
