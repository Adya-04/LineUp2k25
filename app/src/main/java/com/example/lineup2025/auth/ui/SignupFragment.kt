package com.example.lineup2025.auth.ui

import android.os.Bundle
import android.util.Log
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
import com.example.lineup2025.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager

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

        binding.regtbtn.setOnClickListener {

            binding.regtbtn.isEnabled = false
            val validateResult = validateUserInput()
            if(validateResult.first){
                authViewModel.signUpUser(getSignupRequestBody())
            }
            else{
                showError(validateResult.second)
            }
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
                            val fullnametxt = binding.name.text.trim().toString()
                            tokenManager.saveToken(response.token, fullnametxt)

                            Log.d("SignupFragment", "Signup successful. Token: ${response.token}")
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

    private fun getSignupRequestBody(): SignUpRequestBody{
        val fullnametxt = binding.name.text.trim().toString()
        val emailtxt = binding.email.text.toString()
        val zealidtxt = binding.zeal.text.trim().toString()
        val passwordtxt = binding.password.text.trim().toString()
        return SignUpRequestBody(emailtxt,passwordtxt,fullnametxt,zealidtxt)
    }

    private fun validateUserInput(): Pair<Boolean,String>{
        val signUpRequestBody = getSignupRequestBody()
        return authViewModel.validateSignupCredentials(signUpRequestBody.name,signUpRequestBody.email,signUpRequestBody.zealId,signUpRequestBody.password)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.regtbtn.isEnabled = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
