package com.example.lineup2025.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lineup2025.databinding.AvatarLayoutBinding

class AvatarAdapter(private val avatars: IntArray) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = AvatarLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(avatars[position])
    }

    override fun getItemCount() = avatars.size

    inner class AvatarViewHolder(private val binding: AvatarLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(avatarResId: Int) {
            binding.avatarImg.setImageResource(avatarResId)
        }
    }
}
