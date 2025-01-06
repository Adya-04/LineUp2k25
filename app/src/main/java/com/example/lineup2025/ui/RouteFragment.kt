package com.example.lineup2025.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lineup2025.RadarView
import com.example.lineup2025.databinding.FragmentRouteBinding
import com.example.lineup2025.model.Location
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.viewmodel.AccessAvatarViewModel
import com.example.lineup2025.viewmodel.RouteViewModel
import com.skyfishjy.library.RippleBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding : FragmentRouteBinding? = null
    private val binding get() = _binding!!
    private var rippleBackground: RippleBackground? = null
    private val accessAvatarViewModel by viewModels<AccessAvatarViewModel>()
    private val routeViewModel by viewModels<RouteViewModel>()
    private lateinit var radarView: RadarView
    private val handler = Handler()

    private val apiRunnable = object : Runnable {
        override fun run() {
            routeViewModel.getRoute()
            handler.postDelayed(this, 5000) // Schedule the next call after 5 seconds
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        radarView = binding.radarview
        radarView.setupViewModel(accessAvatarViewModel, viewLifecycleOwner)
        binding.rippleBg.startRippleAnimation()
        setupObservers()
        routeViewModel.getRoute()
    }

    private fun setupObservers() {
        routeViewModel.getRouteResponseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    val bodyResponse = it.data
                    bodyResponse?.let { response ->
                        Log.d("Route Fragment", "$response")
                        val users = response.nearestUsers.map { user ->
                            Log.d("Route Fragment", "Inside this block")
                            val location = Location(user.name, user.avatar, user.distance, user.direction)
                            Log.d("Route Fragment", "Location Created: $location")
                            location
                        }
                        // Update RadarView with the new route data
                        radarView.setUsers(users)
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                    Log.d("RouteFragment", "Error: ${it.message}")
                }

                is NetworkResult.Loading -> {
//                    showLoading()
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Start the periodic API call
        handler.post(apiRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Stop the periodic API call
        handler.removeCallbacks(apiRunnable)
    }
}