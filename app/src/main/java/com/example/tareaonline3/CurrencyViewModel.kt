package com.example.tareaonline3

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class CurrencyViewModel : ViewModel() {
    private val apiService = ExchangeRateApiService.retrofit.create(ExchangeRateApi::class.java)

    private val _exchangeRate = MutableLiveData<Double?>()
    val exchangeRate: LiveData<Double?> get() = _exchangeRate

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadExchangeRate() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getEurToUsdRate()
                response.rates["USD"]?.let { rate ->
                    _exchangeRate.value = rate
                    _error.value = null
                } ?: run {
                    _error.value = "No se encontró tasa USD"
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun convertCurrency(amount: Double, isEurToUsd: Boolean): String? {
        return _exchangeRate.value?.let { rate ->
            if (isEurToUsd) {
                "%.2f EUR = %.2f USD".format(amount, amount * rate)
            } else {
                "%.2f USD = %.2f EUR".format(amount, amount / rate)
            }
        }
    }

    private fun handleError(e: Exception) {
        _error.value = when (e) {
            is SocketTimeoutException -> "Tiempo de espera agotado"
            is IOException -> "Error de conexión"
            is HttpException -> "Error del servidor (${e.code()})"
            else -> "Error: ${e.localizedMessage}"
        }
        _exchangeRate.value = null
    }
}