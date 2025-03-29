package com.example.sugarate.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.sugarate.database.DatabaseInstance
import com.example.sugarate.database.dao.RecipeDao
import com.example.sugarate.database.entities.Recipe

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeDao: RecipeDao = DatabaseInstance.getDatabase(application).recipeDao()

    val recipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()
}