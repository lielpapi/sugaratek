package com.example.sugarate.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarate.database.entities.Recipe
import com.example.sugarate.databinding.FragmentRecipeBinding
import com.example.sugarate.recipesApiService.RecipeResponse
import com.example.sugarate.recipesApiService.RecipesApiService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

//import com.bumptech.glide.Glide

class RecipeAdapter(private val clickListener: RecipeClickListener) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = FragmentRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
    }


    class RecipeViewHolder(private val binding: FragmentRecipeBinding, private val clickListener: RecipeClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RecipesApiService::class.java)
            service.findRecipeFromText(recipe.name,fields = "name,rating",apiKey="AIzaSyBsV4dpcOTGvGNpk3C8Zdm_viZAGui4C1k").enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    if (response.isSuccessful) {
                        val rating = response.body()?.candidates?.firstOrNull()?.rating
                        // Update your UI with the rating

                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    // Handle failure
                    }
            })

            binding.textViewDishName.text = recipe.name
            binding.textViewRecipeMaterials.text = buildString {
                append( recipe.materials)
            }

            binding.textViewRecipePreparation.text = buildString {
                append(recipe.preparation)
            }

            binding.textViewRecipeCalories.text = buildString {
                append(recipe.calories.toString())
                append("\nCalories")

            }


            Picasso.get().invalidate(recipe.dishPhoto)
            Picasso.get()
                .load(recipe.dishPhoto)
                .into(binding.imageViewPhoto as ImageView)

            // Set up click listener for the "Add Comment" button
            binding.buttonAddComment.setOnClickListener {
                clickListener.onAddCommentClicked(recipe.name)
            }

            binding.buttonViewComments.setOnClickListener {
                clickListener.onViewCommentsClicked(recipe.name)
            }
        }
    }

    interface RecipeClickListener {
        fun onAddCommentClicked(dishName: String)
        fun onViewCommentsClicked(dishName: String)
    }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
}