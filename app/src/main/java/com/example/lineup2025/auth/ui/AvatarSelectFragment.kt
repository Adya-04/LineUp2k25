package com.example.lineup2025.auth.ui

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.lineup2025.utils.HorizontalMarginItemDecoration
import com.example.lineup2025.R
import com.example.lineup2025.adapters.AvatarAdapter
import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.viewmodel.AvatarSelectViewModel
import com.example.lineup2025.databinding.FragmentAvatarSelectBinding
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class AvatarSelectFragment : Fragment() {

    private var _binding: FragmentAvatarSelectBinding? = null
    private val binding get() = _binding!!

    private var currentVisiblePosition = 0
    private var visibleImage : Int = 0

    private val avatarSelectViewModel by viewModels<AvatarSelectViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAvatarSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characters = intArrayOf(
            R.drawable.red_avatar,
            R.drawable.pink_avatar,
            R.drawable.yellow_avatar,
            R.drawable.small_avatar,
            R.drawable.grey_avatar,
            R.drawable.blue_avatar,
            R.drawable.brown_avatar,
            R.drawable.green_avatar
        )
        setupViewPager(characters)

        binding.nextBtn.setOnClickListener {
            avatarSelectViewModel.storeAvatar(AvatarRequest(visibleImage))
        }
        setupObservers()
    }

    private fun setupViewPager(characters: IntArray) {
        val viewPager = binding.VP
        viewPager.adapter = AvatarAdapter(characters)
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        binding.VP.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val transformation = CompositePageTransformer()
        transformation.addTransformer(MarginPageTransformer(40))
        transformation.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.40f + r * 0.60f
            page.scaleX = 0.40f + r * 0.60f
            page.translationX = -pageTranslationX * position
            val scaleFactor = 0.5f + r * 0.5f  // Adjust the scaling factor for a larger portion
            page.scaleY = scaleFactor
            page.scaleX = scaleFactor
        }

        val offsetPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin).toInt()
                .dpToPx(resources.displayMetrics)
        binding.VP.setPadding(offsetPx, 0, offsetPx, 0)
        binding.VP.setPageTransformer(transformation)
        val itemDecoration = HorizontalMarginItemDecoration(
            this, R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.VP.addItemDecoration(itemDecoration)
        binding.VP.offscreenPageLimit = 2

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // This method is called when a new page becomes selected
                // Update the currentVisiblePosition
                currentVisiblePosition = position
                // You can now use this position to determine the mapped image number
                visibleImage = getVisibleMappedImage()

            }
        })
    }

    private fun getVisibleMappedImage(): Int {
        // Calculate the position of the currently visible item
        return currentVisiblePosition + 1
    }

    private fun setupObservers() {
        avatarSelectViewModel.storeAvatarResponseLiveData.observe(viewLifecycleOwner, Observer {
            hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    showToast("Avatar selected successfully")
                    findNavController().navigate(R.id.action_characterSelectFragment_to_rulesFragment)
                }

                is NetworkResult.Error -> {
                    showToast(it.message ?: "Error occurred")
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }
            }
        })
    }

    private fun showLoading() {
        binding.mainLayout.alpha = 0.5f // Dim the background
        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        binding.VP.isEnabled = false
    }

    private fun hideLoading() {
        binding.mainLayout.alpha = 1.0f // Reset dimming
        binding.progressBar.visibility = View.GONE // Hide progress bar

        binding.VP.isEnabled = true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(displayMetrics: DisplayMetrics): Int =
        (this * displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


