package com.arctouch.codechallenge.scenes.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.MoviesCache
import com.arctouch.codechallenge.data.MoviesRepository
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val repository: MoviesRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var currentPage = 1L

    private val _loadingMovies = MutableLiveData<Boolean>()
    private val _foundError = MutableLiveData<Boolean>()
    private val _moviesList = MutableLiveData<ArrayList<Movie>>()

    val loadingMovies: LiveData<Boolean>
        get() = _loadingMovies

    val moviesList: LiveData<ArrayList<Movie>>
        get() = _moviesList

    val foundError: LiveData<Boolean>
        get() = _foundError

    init {
        handleMovieList(false)
    }

    fun handleMovieList(isRefresh: Boolean) {
        if (!isRefresh) _loadingMovies.postValue(true)

        val upcomingDisposable = repository.getGenres()
            .flatMap { genreResponse -> Observable.just(genreResponse.genres) }
            .map { genres -> MoviesCache.cacheGenres(genres) }
            .concatMap { repository.getMovies() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { upcomingMovies ->
                    val moviesWithGenres = ArrayList(upcomingMovies.results.map { movie ->
                        movie.copy(genres = MoviesCache.genres.filter { movie.genreIds?.contains(it.id) == true })
                    })
                    _foundError.postValue(false)
                    _loadingMovies.postValue(false)
                    _moviesList.postValue(moviesWithGenres)
                },
                onError = {
                    _loadingMovies.postValue(false)
                    _foundError.postValue(true)
                }
            )

        compositeDisposable.add(upcomingDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}