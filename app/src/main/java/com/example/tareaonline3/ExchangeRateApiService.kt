package com.example.tareaonline3

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRateApiService {
    private const val BASE_URL = "https://api.frankfurter.app/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}