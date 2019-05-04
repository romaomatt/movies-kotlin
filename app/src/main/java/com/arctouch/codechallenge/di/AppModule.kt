package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.data.MoviesRepository
import com.arctouch.codechallenge.scenes.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { MoviesRepository(get()) }
    viewModel { HomeViewModel(get()) }
}