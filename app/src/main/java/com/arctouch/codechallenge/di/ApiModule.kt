package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val apiModule = module {
    single {
        createTmdbApi(
            createRetrofit(
                createOkHttpClient(
                    createLoggerInterceptor(),
                    createHeaderInterceptor()
                )
            )
        )
    }
}

fun createLoggerInterceptor(): Interceptor {
    return HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}

fun createHeaderInterceptor(): Interceptor {
    return Interceptor { chain ->
        val oldRequest = chain.request()

        val newUrl = oldRequest.url().newBuilder()
            .addQueryParameter("api_key", BuildConfig.API_KEY)
            .build()

        val newRequest = oldRequest
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }
}

fun createOkHttpClient(logging: Interceptor, header: Interceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(TmdbApi.TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TmdbApi.TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(header)
        .addInterceptor(logging)
        .build()
}

fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(TmdbApi.URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
}

fun createTmdbApi(retrofit: Retrofit): TmdbApi {
    return retrofit.create(TmdbApi::class.java)
}