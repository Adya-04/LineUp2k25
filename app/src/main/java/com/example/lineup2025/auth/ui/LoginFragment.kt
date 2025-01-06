package com.example.lineup2025.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.BottomNavigationActivity
import com.example.lineup2025.MainActivity
import com.example.lineup2025.R
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.viewmodel.AuthViewModel
import com.example.lineup2025.databinding.FragmentLoginBinding
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {

            binding.loginBtn.isEnabled = false
            val validateResult = validateLoginUserInput()
            if (validateResult.first) {
                authViewModel.login(getLoginRequest())
            } else {
                showError(validateResult.second)
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        authViewModel.loginResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        if (response.message == "Login successful") {
                            tokenManager.saveToken(response.token,response.name)
                            tokenManager.saveScannedQRCodes(response.scannedCodes.toSet())

                            showToast("Login Successful")
                            startActivity(
                                Intent(activity as MainActivity,
                                    BottomNavigationActivity::class.java)
                            )
                            (activity as MainActivity).finish()
                            //fun deletepreviousstate
                        } else {
                            showToast("User Not Found!")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                    binding.loginBtn.isEnabled = true
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                else -> {}
            }
        })
    }

    private fun getLoginRequest(): LoginRequestBody {
        val zealId = binding.zeal.text.trim().toString()
        val password = binding.password.text.trim().toString()
        return LoginRequestBody(password, zealId)
    }

    private fun validateLoginUserInput(): Pair<Boolean, String> {
        val getLoginRequest = getLoginRequest()
        return authViewModel.validateLoginCredentials(
            getLoginRequest.zealId,
            getLoginRequest.password
        )
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


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.loginBtn.isEnabled = true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}