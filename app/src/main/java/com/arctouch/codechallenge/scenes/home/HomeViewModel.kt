package com.arctouch.codechallenge.scenes.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.MoviesCache
import com.arctouch.codechallenge.data.MoviesRepository
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val repository: MoviesRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var currentPage = 1L
    val loadingMovies = MutableLiveData<Boolean>()
    val moviesList = MutableLiveData<ArrayList<Movie>>()

    fun handleUpcomingMovies() {
        loadingMovies.postValue(true)

        val upcomingDisposable = repository.getGenres()
            .flatMap { genreResponse -> Observable.just(genreResponse.genres) }
            .map { genres -> MoviesCache.cacheGenres(genres) }

            .concatMap { repository.getMovies() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { upcomingMovies ->
                val moviesWithGenres = ArrayList(upcomingMovies.results.map { movie ->
                    movie.copy(genres = MoviesCache.genres.filter { movie.genreIds?.contains(it.id) == true })
                })
                loadingMovies.postValue(false)
                moviesList.postValue(moviesWithGenres)
            }

        compositeDisposable.add(upcomingDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }


}