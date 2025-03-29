package com.example.sugarate.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.sugarate.database.DatabaseInstance
import com.example.sugarate.database.entities.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao = DatabaseInstance.getDatabase(application).recipeDao()


    fun getRecipeById(placeId: Int): LiveData<Recipe> {
        return recipeDao.getRecipeById(placeId)
    }

}