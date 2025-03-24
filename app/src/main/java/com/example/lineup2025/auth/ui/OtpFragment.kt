package com.example.lineup2025.auth.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.R
import com.example.lineup2025.auth.model.verifyOtpRequest
import com.example.lineup2025.auth.viewmodel.OtpViewModel
import com.example.lineup2025.databinding.FragmentOtpBinding
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val otpViewModel by viewModels<OtpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addTextChangeListener()
        setupObservers()

        val email = arguments?.getString("email")?: ""
        binding.confirmBtn.setOnClickListener {
            binding.confirmBtn.isEnabled = false
            val typedOtp =
                (binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString()
                        + binding.otpEditText3.text.toString() + binding.otpEditText4.text.toString()
                        + binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString())

           val validateResult = otpViewModel.validateOtp(typedOtp)
            if(validateResult.first){
                otpViewModel.verifyOtp(verifyOtpRequest(email,typedOtp))
            }
            else{
                showError(validateResult.second)
            }
        }
    }

private fun setupObservers(){
    otpViewModel.verifyOtpResponseLivedata.observe(viewLifecycleOwner, Observer {
        when(it){
            is NetworkResult.Success ->{
                val bodyResponse = it.data
                bodyResponse?.let {response->
                    if(response.message == "OTP verified successfully"){
                        showToast("OTP verified successfully")
                        findNavController().navigate(R.id.action_otpFragment_to_characterSelectFragment)
                    }else{
                        showToast("Incorrect OTP")
                        binding.confirmBtn.isEnabled = true
                    }
                }
            }
            is NetworkResult.Error -> {
                showToast(it.message.toString())
                binding.confirmBtn.isEnabled = true
            }
            is NetworkResult.Loading -> {
                //load
            }
        }
    })
}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        binding.confirmBtn.isEnabled = true
    }

    private fun addTextChangeListener() {
        val otpEditTexts = listOf(
            binding.otpEditText1,
            binding.otpEditText2,
            binding.otpEditText3,
            binding.otpEditText4,
            binding.otpEditText5,
            binding.otpEditText6
        )

        for (i in otpEditTexts.indices) {
            otpEditTexts[i].addTextChangedListener(EditTextWatcher(otpEditTexts[i]))

            otpEditTexts[i].setOnKeyListener { v, keyCode, event ->
                if (event.action == android.view.KeyEvent.ACTION_DOWN && keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                    if (otpEditTexts[i].text.isEmpty() && i > 0) {
                        otpEditTexts[i - 1].setText("") // Clear previous box
                        otpEditTexts[i - 1].requestFocus() // Move focus to previous box
                    }
                }
                false
            }
        }
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (view) {
                binding.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                binding.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                binding.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                binding.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                binding.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                binding.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}