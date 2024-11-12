package com.example.lineup2025.auth.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lineup2025.MainActivity
import com.example.lineup2025.auth.repository.SignUpRepository
import com.example.lineup2025.network.RetrofitApi.apiInterface
import com.example.lineup2025.auth.viewmodel.SignUpViewModel
import com.example.lineup2025.auth.viewmodel.SignUpViewModelFactory
import com.example.lineup2025.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(SignUpRepository(apiInterface))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar

        sharedPreferences = getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)

        binding.regtbtn.setOnClickListener {
            registration()
        }

        setupObservers()
    }

    private fun setupObservers() {
        signUpViewModel.signUpResponse.observe(this) { response ->
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    if (it.message == "Signup successful") {
                        sharedPreferences.edit().putString("Token", it.token).apply()
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Zeal Id is already registered", Toast.LENGTH_SHORT).show()
            }
            signUpViewModel.loading.observe(this) { isLoading ->
                if (isLoading) showLoading() else hideLoading()
            }
        }
    }

    private fun registration() {
        binding.regtbtn.isEnabled = false
        binding.regText.isEnabled = false

        val fullnametxt = binding.name.text.trim().toString()
        val emailtxt = binding.email.text.toString()
        val zealidtxt = binding.zeal.text.trim().toString()
        val passwordtxt = binding.password.text.trim().toString()

        if (fullnametxt.isEmpty() || emailtxt.isEmpty() || zealidtxt.isEmpty() || passwordtxt.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            binding.regtbtn.isEnabled = true
            binding.regText.isEnabled = true
        } else if (zealidtxt.length < 6) {
            Toast.makeText(this, "Zeal Id must have 6 characters", Toast.LENGTH_SHORT).show()
            binding.regtbtn.isEnabled = true
            binding.regText.isEnabled = true
        } else if (!validateEmail(emailtxt)) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            binding.regtbtn.isEnabled = true
            binding.regText.isEnabled = true
        } else {
            showLoading()
//            signUpViewModel.signUp(emailtxt, passwordtxt, fullnametxt, zealidtxt)
            try {
                signUpViewModel.signUp(emailtxt, passwordtxt, fullnametxt, zealidtxt)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace() // Log the exception stack trace
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        binding.signUpRel.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        binding.signUpRel.visibility = View.VISIBLE
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}