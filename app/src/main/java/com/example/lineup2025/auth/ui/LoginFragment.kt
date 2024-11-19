package com.example.lineup2025.auth.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.R
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.viewmodel.AuthViewModel
import com.example.lineup2025.databinding.FragmentLoginBinding
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val authViewModel by viewModels <AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)

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
        val loginRequest = LoginRequestBody(password = password, zealId = zealId)
        authViewModel.login(loginRequest)
    }

    private fun setupObservers() {
        authViewModel.loginResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val editor = sharedPreferences.edit()
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        if (response.message == "Login successful") {
                            editor.putString("Token", response.token)
                            editor.putString("Name", response.name)
                            editor.putStringSet("scannedQRSet", HashSet(response.scannedCodes))
                            editor.apply()

                            showToast("Login Successful")
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        } else {
                            showToast("User Not Found!")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                else -> {}
            }
        })
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}