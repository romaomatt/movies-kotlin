package com.arctouch.codechallenge.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MoviesDataSource(
    private val moviesRepository: MoviesRemoteRepository,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Movie>() {

    private var retryCompletable: Completable? = null
    private val _state = MutableLiveData<MovieListStateEnum>()

    val state: LiveData<MovieListStateEnum> = _state

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        _state.postValue(MovieListStateEnum.LOADING)

        val initialDisposable = moviesRepository.getGenres()
            .flatMap { genreResponse -> Observable.just(genreResponse.genres) }
            .map { genres -> MoviesCache.cacheGenres(genres) }
            .concatMap { moviesRepository.getUpcomingMovies(1) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { response ->
                    _state.postValue(MovieListStateEnum.COMPLETE)
                    callback.onResult(joinMovieResultWithGenres(response.results), null, 2)
                },
                onError = {
                    _state.postValue(MovieListStateEnum.ERROR)
                    setRetryOnError(Action { loadInitial(params, callback) })
                }
            )

        compositeDisposable.add(initialDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        _state.postValue(MovieListStateEnum.LOADING)

        val nextDisposable = moviesRepository.getUpcomingMovies(params.key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { response ->
                    _state.postValue(MovieListStateEnum.COMPLETE)
                    callback.onResult(joinMovieResultWithGenres(response.results), params.key + 1)
                },
                onError = {
                    _state.postValue(MovieListStateEnum.ERROR)
                    setRetryOnError(Action { loadAfter(params, callback) })
                }
            )

        compositeDisposable.add(nextDisposable)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}

    private fun joinMovieResultWithGenres(results: List<Movie>): List<Movie> {
        return results.map { movie ->
            movie.copy(genres = MoviesCache.genres.filter { movie.genreIds?.contains(it.id) == true })
        }
    }

    private fun setRetryOnError(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    fun retryOnError() {
        retryCompletable?.let { retry ->
            val retryDisposable = retry
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

            compositeDisposable.add(retryDisposable)
        }
    }
}