package com.example.lineup2025

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lineup2025.databinding.FragmentQrScannerBinding
import com.example.lineup2025.model.QRScannerRequest
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.utils.TokenManager
import com.example.lineup2025.viewmodel.QRScannerViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class QRScannerFragment : Fragment() {

    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    private val qrScannerViewModel by viewModels<QRScannerViewModel>()

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScannerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView = binding.barcodeScanner
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(requireActivity().intent)
        barcodeView.decodeContinuous(callback)
        setupObservers()

        // Start the countdown timer
        val targetDate = "2024-12-31"
        val targetTime = "16:00:00"
        startCountdownToDateTime(targetDate, targetTime)
    }

private val callback = object : BarcodeCallback{
    override fun barcodeResult(result: BarcodeResult) {
        barcodeView.pause()
        result.text?.let { qrCode ->
            saveQRCode(qrCode)
            Log.d("QRScanner","result obtained as $qrCode")
        }
    }
    override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
}

    private fun saveQRCode(qrCode: String) {
        val scannedCodes = tokenManager.getScannedQRCodes().toMutableSet()
        if (qrCode !in scannedCodes) {
            scannedCodes.add(qrCode)
            tokenManager.saveScannedQRCodes(scannedCodes)
            sendQRtoBackend(qrCode)
            Log.d("QRScanner", "QR code saved: $qrCode")
        } else {
            popup("Oops! Duplicate Member")
        }
    }

    private fun sendQRtoBackend(qrCode: String) {
        val scanQRRequest = QRScannerRequest(qrCode)
        qrScannerViewModel.sendQR(scanQRRequest)
    }

    private fun setupObservers() {
        qrScannerViewModel.qrScannerResponseLiveData.observe(viewLifecycleOwner, Observer {
//            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        if(response.message == "QR Code scanned successfully"){
                            popup("Member Found!")
                        }
                        else{
                            val msg = response.message
                            popup(msg)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Log.d("QRScannerFragment" ,it.message.toString())
//                    popup("Something went wrong")
                    popup("${it.message}")
                }

                is NetworkResult.Loading -> {
//                    showLoading()
                }
            }
        })
    }

    private fun startCountdownToDateTime(targetDate: String, targetTime: String) {
        // Parse the target date and time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val targetDateTime = dateFormat.parse("$targetDate $targetTime")

        // Calculate milliseconds until the target date and time
        val millisecondsUntilTarget = targetDateTime.time - System.currentTimeMillis()

        // Create and start the countdown timer
        countDownTimer = object : CountDownTimer(millisecondsUntilTarget, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (1000 * 60 * 60)
                val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = ((millisUntilFinished % (1000 * 60 * 60)) % (1000 * 60) / 1000)

                // Update the TextView with the remaining time
                binding.timeleft.text = if (hours > 0) {
                    String.format("%02d hours %02d minutes", hours, minutes)
                } else {
                    String.format("%02d minutes %02d seconds", minutes, seconds)
                }
            }

            override fun onFinish() {
                // Update the TextView to indicate the countdown has finished
                binding.timeleft.text = "00:00:00"
                // Perform any additional action when the countdown finishes
            }
        }.start()
    }

    private fun popup(msg: String){
        val popup = Dialog(requireContext())
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popup.setCancelable(false)
        popup.setContentView(R.layout.member_found)
        val message = popup.findViewById<TextView>(R.id.message)
        val reset = popup.findViewById<Button>(R.id.reset_Button)
        popup.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        message.text = msg

        reset.setOnClickListener {
            barcodeView.resume()
            //  scanningEnabled = true
            popup.dismiss()
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

}