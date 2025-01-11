package com.example.lineup2025.model

data class LeaderboardModel(
    val users: List<LeaderboardUsers>
)

data class LeaderboardUsers(
    val name: String,
    val membersFound: Int,
    val avatar: Int,
)