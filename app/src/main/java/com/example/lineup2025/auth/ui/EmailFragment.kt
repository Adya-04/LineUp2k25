package com.example.lineup2025.auth.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.auth.model.generateOtpRequest
import com.example.lineup2025.auth.viewmodel.OtpViewModel
import com.example.lineup2025.databinding.FragmentEmailBinding
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!

    private val otpViewModel by viewModels<OtpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            binding.nextBtn.isEnabled = false
            val email = binding.email.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please enter valid email")
                binding.nextBtn.isEnabled = true
                return@setOnClickListener
            }

            otpViewModel.generateOtp(generateOtpRequest(email))
            observeOtpResponse(email)
        }
    }

    private fun  observeOtpResponse(email: String) {
        otpViewModel.generateOtpResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        if (response.message == "OTP sent successfully") {
                            showToast("OTP sent successfully")
                            val action = EmailFragmentDirections.actionEmailFragmentToOtpFragment(email)
                            findNavController().navigate(action)
                        } else {
                            showToast("Issue in Otp generation")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                    binding.nextBtn.isEnabled = true
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun hideLoading() {
        binding.mainLayout.alpha = 1.0f // Reset dimming
        binding.progressBar.visibility = View.GONE // Hide progress bar

        binding.email.isEnabled = true
    }
    private fun showLoading() {
        binding.mainLayout.alpha = 0.5f // Dim the background
        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        binding.email.isEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}