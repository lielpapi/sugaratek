package com.example.sugarate.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sugarate.R
import com.example.sugarate.databinding.FragmentHomePageBinding
import com.example.sugarate.recipes.RecipeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomePageFragment : Fragment(), RecipeAdapter.RecipeClickListener {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomePageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_home_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize your adapter
        val recipeAdapter = RecipeAdapter(this)

        // Setup RecyclerView
        binding.recyclerViewRecipes.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Observe changes in the database and submit list to adapter
        viewModel.recipes.observe(viewLifecycleOwner) { recipe ->
            recipeAdapter.submitList(recipe)
        }
        val fabAddPost = view.findViewById<FloatingActionButton>(R.id.fab_add_post)
        fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_postFragment)
        }

    }

    override fun onAddCommentClicked(dishName: String) {
        val bundle = bundleOf("dishName" to dishName)
        findNavController().navigate(R.id.action_homePageFragment_to_postFragment, bundle)
    }

    override fun onViewCommentsClicked(dishName: String) {
        val bundle = bundleOf("dishName" to dishName)
        findNavController().navigate(R.id.action_homePageFragment_to_commentsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}