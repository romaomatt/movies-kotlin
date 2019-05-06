package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.data.MoviesDataSource
import com.arctouch.codechallenge.data.MoviesDataSourceFactory
import com.arctouch.codechallenge.data.MoviesRemoteRepository
import com.arctouch.codechallenge.scenes.details.DetailsViewModel
import com.arctouch.codechallenge.scenes.home.HomeViewModel
import com.arctouch.codechallenge.scenes.main.MainViewModel
import com.arctouch.codechallenge.scenes.search.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { MoviesRemoteRepository(get()) }
    single { MoviesDataSource(get(), CompositeDisposable()) }
    single { MoviesDataSourceFactory(get()) }

    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { (movieId: Int) -> DetailsViewModel(get(), movieId) }
    viewModel { SearchViewModel(get()) }
}