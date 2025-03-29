package com.example.sugarate.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sugarate.R
import com.example.sugarate.databinding.FragmentNavigationBarBinding

class NavigationBarFragment : Fragment() {
    private var _binding: FragmentNavigationBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNavigationBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            Log.d("TAG","NavigationBarFragment")
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(R.id.homePageFragment)
                    true
                }
                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
        }
}