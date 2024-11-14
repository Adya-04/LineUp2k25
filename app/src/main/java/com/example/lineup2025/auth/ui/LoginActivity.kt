package com.example.lineup2025.auth.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lineup2025.MainActivity
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.repository.LoginRepository
import com.example.lineup2025.auth.viewmodel.LoginViewModel
import com.example.lineup2025.auth.viewmodel.LoginViewModelFactory
import com.example.lineup2025.databinding.ActivityLoginBinding
import com.example.lineup2025.network.RetrofitApi

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(LoginRepository(RetrofitApi.apiInterface))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)

        binding.loginBtn.setOnClickListener {
            logIn()
        }

        setupObservers()
    }
    private fun logIn() {
        val zealId = binding.zeal.text.trim().toString()
        val password = binding.password.text.trim().toString()

        if (zealId.isEmpty() || password.isEmpty()) {
            showToast("Please enter your ZealId and Password")
            return
        }

        showLoading()
        val loginRequest = LoginRequestBody(password = password, zealId = zealId)
        loginViewModel.login(loginRequest)
    }

    private fun setupObservers() {
        loginViewModel.loginResponse.observe(this) { response ->
            if (response.isSuccessful) {
                val editor = sharedPreferences.edit()
                val bodyResponse = response.body()
                bodyResponse?.let {
                    if (it.message == "Login successful") {
                        editor.putString("Token", it.token)
                        editor.putString("Name", it.name)
                        editor.putStringSet("scannedQRSet", HashSet(it.scannedCodes))
                        editor.apply()

                        showToast("Login Successful")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        showToast("User Not Found!")
                    }
                }
            } else {
                showToast("Login Failed")
            }
        }

        loginViewModel.loading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }
    }

    private fun showLoading() {
        binding.mainLayout.alpha = 0.5f // Dim the background
        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        binding.zeal.isEnabled = false
        binding.password.isEnabled = false
    }

    private fun hideLoading() {
        binding.mainLayout.alpha = 1.0f // Reset dimming
        binding.progressBar.visibility = View.GONE // Hide progress bar

        binding.zeal.isEnabled = true
        binding.password.isEnabled = true
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}