package com.example.tareaonline3

// ExchangeRateResponse.kt
// ExchangeRateResponse.kt
data class ExchangeRateResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)