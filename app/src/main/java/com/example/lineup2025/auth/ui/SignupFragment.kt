package com.example.lineup2025.auth.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.R
import com.example.lineup2025.auth.repository.SignUpRepository
import com.example.lineup2025.auth.viewmodel.SignUpViewModel
import com.example.lineup2025.auth.viewmodel.SignUpViewModelFactory
import com.example.lineup2025.databinding.FragmentSignupBinding
import com.example.lineup2025.network.RetrofitApi.apiInterface

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(SignUpRepository(apiInterface))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)

        binding.regtbtn.setOnClickListener {
            registration()
        }

        setupObservers()
    }

    private fun setupObservers() {
        signUpViewModel.signUpResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.let {
                    if (it.message == "Signup successful") {
                        sharedPreferences.edit().putString("Token", it.token).apply()
                        Toast.makeText(requireContext(), "Registered Successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signupFragment_to_characterSelectFragment)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Zeal Id is already registered", Toast.LENGTH_SHORT).show()
            }
        }
        signUpViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
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
            showError("Please fill all the fields")
        } else if (zealidtxt.length < 6) {
            showError("Zeal Id must have 6 characters")
        } else if (!validateEmail(emailtxt)) {
            showError("Please enter a valid email")
        } else {
            showLoading()
            try {
                signUpViewModel.signUp(emailtxt, passwordtxt, fullnametxt, zealidtxt)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("SignupFragment", "Error during signup", e)
                e.printStackTrace() // Log the exception stack trace
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.regtbtn.isEnabled = true
        binding.regText.isEnabled = true
    }

    private fun showLoading() {
        binding.mainLayout.alpha = 0.5f // Dim the background
        binding.progressBar.visibility = View.VISIBLE // Show progress bar


        // Disable text fields
        binding.name.isEnabled = false
        binding.email.isEnabled = false
        binding.zeal.isEnabled = false
        binding.password.isEnabled = false
    }

    private fun hideLoading() {
        binding.mainLayout.alpha = 1.0f // Reset dimming
        binding.progressBar.visibility = View.GONE // Hide progress bar


        // Re-enable text fields
        binding.name.isEnabled = true
        binding.email.isEnabled = true
        binding.zeal.isEnabled = true
        binding.password.isEnabled = true
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}