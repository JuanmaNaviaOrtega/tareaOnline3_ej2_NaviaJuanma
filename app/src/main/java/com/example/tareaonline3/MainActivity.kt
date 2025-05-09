package com.example.tareaonline3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.tareaonline3.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels()
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadCurrencyImage()
        setupObservers()
        setupListeners()

        viewModel.loadExchangeRate()
        performConversion()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun loadCurrencyImage() {
        Picasso.get()
            .load("https://images.unsplash.com/photo-1604594849809-dfedbc827105?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80")
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivCurrency)
    }

    private fun setupObservers() {
        viewModel.exchangeRate.observe(this) { rate ->
            rate?.let {
                binding.tvCurrentRate.text = getString(R.string.current_rate, it)
                performConversion() // Actualizar conversión cuando cambia la tasa
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.tvCurrentRate.text = getString(R.string.error_loading)
                binding.tvResult.text = ""
            }
        }
    }

    private fun setupListeners() {
        // Listener para cambios en el campo de texto
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    performConversion()
                }
            }
        })

        // Listener para cambios en los RadioButtons
        binding.rgConversionType.setOnCheckedChangeListener { _, _ ->
            performConversion()
        }
    }

    private fun performConversion() {
        val amountText = binding.etAmount.text.toString()
        if (amountText.isNotEmpty() && amountText != "-" && amountText != ".") {
            try {
                val amount = amountText.toDouble()
                val isEurToUsd = binding.rbEurToUsd.isChecked

                viewModel.convertCurrency(amount, isEurToUsd)?.let { result ->
                    isUpdating = true
                    binding.tvResult.text = result
                    isUpdating = false
                }
            } catch (e: NumberFormatException) {
                binding.tvResult.text = ""
            }
        } else {
            binding.tvResult.text = ""
        }
    }
}