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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.R
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.viewmodel.AuthViewModel
import com.example.lineup2025.databinding.FragmentSignupBinding
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    private val authViewModel by viewModels<AuthViewModel>()

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
        authViewModel.signUpResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        if (response.message == "Signup successful") {
                            sharedPreferences.edit().putString("Token", response.token).apply()
                            showToast("Registered Successfully")
                        findNavController().navigate(R.id.action_signupFragment_to_characterSelectFragment)

                        } else {
                            showToast("Zeal Id is already registered")
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
            try {
                val signupRequest = SignUpRequestBody(emailtxt, passwordtxt, fullnametxt, zealidtxt)
                authViewModel.signUpUser(signupRequest)
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
