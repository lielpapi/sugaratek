package com.example.sugarate.recipesApiService

data class RecipeResponse(val candidates: List<RecipeDetails>)
data class RecipeDetails(val name: String, val rating:Double)