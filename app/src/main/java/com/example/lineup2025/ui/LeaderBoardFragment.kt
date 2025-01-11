package com.example.lineup2025.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lineup2025.R
import com.example.lineup2025.adapters.LeaderboardAdapter
import com.example.lineup2025.databinding.FragmentLeaderBoardBinding
import com.example.lineup2025.model.LeaderboardUsers
import com.example.lineup2025.utils.NetworkResult
import com.example.lineup2025.viewmodel.LeaderboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderBoardFragment : Fragment() {

    private var _binding : FragmentLeaderBoardBinding? = null
    val binding get() = _binding!!

    private lateinit var characters: IntArray

    private lateinit var leaderboardAdapter: LeaderboardAdapter

    private val leaderboardViewModel by viewModels<LeaderboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderBoardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        characters = intArrayOf(
            R.drawable.red_avatar,
            R.drawable.pink_avatar,
            R.drawable.yellow_avatar,
            R.drawable.small_avatar,
            R.drawable.grey_avatar,
            R.drawable.blue_avatar,
            R.drawable.brown_avatar,
            R.drawable.green_avatar
        )

        setupRecyclerView()
        observeLeaderboardData()

        leaderboardViewModel.getPlayers()

        binding.swipeLayout.setOnRefreshListener {
            leaderboardViewModel.getPlayers()
            Toast.makeText(requireContext(), "Leaderboard Refreshed!!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(emptyList())
        binding.leaderboardRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderboardAdapter
        }
    }

    private fun observeLeaderboardData() {
        leaderboardViewModel.leaderboardLiveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val leaderboardUsers = result.data?.users ?: emptyList()
                    updateLeaderboard(leaderboardUsers)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                   //loading state
                }
            }
            binding.swipeLayout.isRefreshing = false
        })
    }

    private fun updateLeaderboard(users: List<LeaderboardUsers>) {
        val leaderboardList = users.map { user ->
            LeaderboardUsers(
                user.name,
                user.membersFound,
                characters[user.avatar - 1]
            )
        }
        leaderboardAdapter.updateData(leaderboardList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}