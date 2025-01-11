package com.example.lineup2025.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lineup2025.databinding.LeaderboardItemBinding
import com.example.lineup2025.model.LeaderboardUsers

class LeaderboardAdapter(private var users: List<LeaderboardUsers>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    inner class LeaderboardViewHolder(private val binding: LeaderboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LeaderboardUsers) {
            binding.leaderboardName.text = item.name
            binding.leaderboardScore.text = item.membersFound.toString()
            binding.playerAvatar.setImageResource(item.avatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding =
            LeaderboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<LeaderboardUsers>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}