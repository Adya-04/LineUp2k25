package com.example.lineup2025.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lineup2025.databinding.FragmentQrCodeBinding
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.viewmodel.QRCodeViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRCodeFragment : Fragment() {

    private var _binding : FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    private val qrcodeViewModel by viewModels<QRCodeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrCodeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrcodeViewModel.getQRCode()

        setupObservers()
    }

    private fun setupObservers() {
        qrcodeViewModel.qrcodeResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        generateQRCode(response.code)
                    }
                    binding.tryAgain.visibility = View.GONE
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                    binding.tryAgain.visibility = View.VISIBLE
                    binding.tryAgain.isEnabled = true
                    binding.tryAgain.setOnClickListener {
                        qrcodeViewModel.getQRCode()
                        binding.tryAgain.visibility = View.GONE
                        binding.tryAgain.isEnabled = false
                    }
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }
            }
        })
    }

    private fun generateQRCode(code: String) {
        try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val bitMap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

            binding.qrCode.setImageBitmap(bitMap) // Set the generated QR code to ImageView
        } catch (e: Exception) {
            showToast("Unable to generate QR")
            Log.e("QRCodeFragment", "Error generating QR", e)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        binding.myQrLayout.alpha = 0.5f // Dim the background
        binding.progressBar.visibility = View.VISIBLE // Show progress bar
    }

    private fun hideLoading() {
        binding.myQrLayout.alpha = 1.0f // Reset dimming
        binding.progressBar.visibility = View.GONE // Hide progress bar
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Prevent screenshots and screen recording
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    override fun onPause() {
        super.onPause()
        // Allow screenshots again when the fragment is not visible
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}