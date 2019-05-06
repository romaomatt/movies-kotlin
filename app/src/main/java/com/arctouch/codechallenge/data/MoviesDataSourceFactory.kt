package com.arctouch.codechallenge.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.arctouch.codechallenge.model.Movie

class MoviesDataSourceFactory(
    private val moviesDataSource: MoviesDataSource
) : DataSource.Factory<Int, Movie>() {

    private val _movieDataSource = MutableLiveData<MoviesDataSource>()
    val movieDataSource: LiveData<MoviesDataSource> = _movieDataSource

    override fun create(): DataSource<Int, Movie> {
        _movieDataSource.postValue(moviesDataSource)
        return moviesDataSource
    }

}
