package com.example.sugarate.recipes


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sugarate.databinding.FragmentRecipeBinding


class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("recipeId")?.let { recipeId ->
            viewModel.getRecipeById(recipeId).observe(viewLifecycleOwner) { recipe ->
                recipe?.let {
                    binding.textViewDishName.text = it.name
                    binding.textViewRecipeMaterials.text = buildString {
                        append("Recipe materials: ")
                        append( it.materials)
                    }

                    binding.textViewRecipePreparation.text = buildString {
                        append("Recipe Preparation: ")
                        append(it.preparation)
                    }
                    binding.textViewRecipeCalories.text = buildString {
                        append(it.calories.toString())
                        append("\nCalories")

                    }

                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
