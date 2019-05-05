package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.util.API_URL
import com.arctouch.codechallenge.util.TIMEOUT
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val apiModule = module {
    single { createTmdbApi() }
}

fun createTmdbApi(): TmdbApi {
    val header = Interceptor { chain ->
        val oldRequest = chain.request()

        val newUrl = oldRequest.url().newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .addQueryParameter("language", Locale.getDefault().toString().replace("_", "-"))
            .build()

        val newRequest = oldRequest
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    val logger = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(header)
        .addInterceptor(logger)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()

    return retrofit.create(TmdbApi::class.java)
}