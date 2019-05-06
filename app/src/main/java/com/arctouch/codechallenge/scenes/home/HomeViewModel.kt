package com.arctouch.codechallenge.scenes.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arctouch.codechallenge.data.MovieListStateEnum
import com.arctouch.codechallenge.data.MoviesDataSource
import com.arctouch.codechallenge.data.MoviesDataSourceFactory
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MOVIE_LIST_SIZE
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel(private val movieDataSourceFactory: MoviesDataSourceFactory) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(MOVIE_LIST_SIZE)
        .build()

    val currentState: LiveData<MovieListStateEnum>
    val moviesList: LiveData<PagedList<Movie>>

    init {
        moviesList = LivePagedListBuilder<Int, Movie>(movieDataSourceFactory, pagedListConfig).build()
        currentState = Transformations.switchMap<MoviesDataSource, MovieListStateEnum>(
            movieDataSourceFactory.movieDataSource,
            MoviesDataSource::state
        )
    }

    fun handleRetry() {
        movieDataSourceFactory.movieDataSource.value?.retryOnError()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}