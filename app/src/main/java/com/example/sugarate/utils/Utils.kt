package com.example.sugarate.utils

import android.app.Application
import com.example.sugarate.database.DatabaseInstance
import com.example.sugarate.database.entities.Recipe
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Utils {
    fun initializeDatabaseWithRecipe(application: Application, imageUrl: String, name: String, materials: String, preparation: String, calories: Int) {
        val storageReference = FirebaseStorage.getInstance().getReference(imageUrl)
        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
            val imageUrl = downloadUri.toString()
            val recipe = Recipe(
                name = name,
                materials = materials,
                preparation = preparation,
                dishPhoto = imageUrl,
                calories = calories,
            )

            // Get your Room database and RecipeDao
            val db = DatabaseInstance.getDatabase(application)
            val recipeDao = db.recipeDao()

            // Check if the recipe with the same name already exists
            recipeDao.getRecipeByName(name).observeForever { existingRecipe ->
                if (existingRecipe == null) {
                    // Recipe doesn't exist, insert it into the database
                    CoroutineScope(Dispatchers.IO).launch {
                        recipeDao.insertRecipe(recipe)
                    }
                }
            }
        }
    }
}