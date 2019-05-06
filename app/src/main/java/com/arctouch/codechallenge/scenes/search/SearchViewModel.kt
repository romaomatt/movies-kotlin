package com.arctouch.codechallenge.scenes.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.MovieListStateEnum
import com.arctouch.codechallenge.data.MoviesCache
import com.arctouch.codechallenge.data.MoviesRemoteRepository
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SearchViewModel(private val repository: MoviesRemoteRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val publishSubject = PublishSubject.create<String?>()
    private val _movieList = MutableLiveData<ArrayList<Movie>>()
    private val _movieState = MutableLiveData<MovieListStateEnum>()
    private var lastQuery = ""

    val movieList: LiveData<ArrayList<Movie>> = _movieList
    val movieState: LiveData<MovieListStateEnum> = _movieState

    fun searchMovie(query: String?) {
        query?.let { queryNotNull ->
            val searchDisposable = publishSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .filter { lastQuery != it && it.length >= 3 }
                .distinctUntilChanged()
                .switchMap { validatedQuery ->
                    lastQuery = validatedQuery
                    _movieState.postValue(MovieListStateEnum.LOADING)
                    repository.searchMovie(validatedQuery)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
                .subscribeBy(
                    onNext = { movieResponse ->
                        if (movieResponse.results.isEmpty()) {
                            _movieState.postValue(MovieListStateEnum.ERROR)
                        } else {
                            val movies = ArrayList(movieResponse.results.map { movie ->
                                movie.copy(genres = MoviesCache.genres.filter { movie.genreIds?.contains(it.id) == true })
                            })
                            _movieState.postValue(MovieListStateEnum.COMPLETE)
                            _movieList.postValue(movies)
                        }
                    },
                    onError = {
                        _movieState.postValue(MovieListStateEnum.ERROR)
                    }
                )

            publishSubject.onNext(queryNotNull)
            compositeDisposable.add(searchDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}