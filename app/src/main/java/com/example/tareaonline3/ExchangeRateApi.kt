package com.example.tareaonline3

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ExchangeRateApi.kt
interface ExchangeRateApi {
    @GET("latest")
    suspend fun getEurToUsdRate(
        @Query("from") baseCurrency: String = "EUR",
        @Query("to") targetCurrency: String = "USD"
    ): ExchangeRateResponse
}