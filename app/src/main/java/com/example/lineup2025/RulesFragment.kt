package com.example.lineup2025

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lineup2025.databinding.FragmentRulesBinding

class RulesFragment : Fragment() {

    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences("LineUpTokens", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("Name", "defaultValue")
        binding.hey.text = "Hey $name"
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_rulesFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}