package com.example.lineup2025

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lineup2025.auth.login.ui.LoginActivity
import com.example.lineup2025.auth.signup.ui.Activity_Register
import com.example.lineup2025.databinding.ActivityWelcomeBinding

class Activity_welcome : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, Activity_Register::class.java)
            startActivity(intent)
            finish()
        }
        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}