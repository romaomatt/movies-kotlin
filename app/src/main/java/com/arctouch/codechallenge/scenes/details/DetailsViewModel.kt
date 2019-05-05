package com.arctouch.codechallenge.scenes.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.MoviesRepository
import com.arctouch.codechallenge.model.Trailer
import com.arctouch.codechallenge.util.YOUTUBE_URL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class DetailsViewModel(repository: MoviesRepository, movieId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _trailerUrl = MutableLiveData<String>()
    private val _loadingTrailer = MutableLiveData<Boolean>()
    private val _errorFound = MutableLiveData<Boolean>()

    val trailer: LiveData<String>
        get() = _trailerUrl

    val loadingTrailer: LiveData<Boolean>
        get() = _loadingTrailer

    val errorFound: LiveData<Boolean>
        get() = _errorFound

    init {
        _loadingTrailer.postValue(true)

        val trailerDisposable = repository.getTrailer(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { trailerResponse ->
                    val trailer = trailerResponse.results.firstOrNull { it.site == "YouTube" }

                    _loadingTrailer.postValue(false)
                    trailer?.let {
                        _trailerUrl.postValue(YOUTUBE_URL + it.key)
                    } ?: run {
                        _errorFound.postValue(true)
                    }
                },
                onError = {
                    _loadingTrailer.postValue(false)
                    _errorFound.postValue(true)
                }
            )

        compositeDisposable.add(trailerDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}