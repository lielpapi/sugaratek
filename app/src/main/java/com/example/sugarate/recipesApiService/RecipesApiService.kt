package com.example.sugarate.recipesApiService

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipesApiService {
    @GET("maps/api/recipe/findrecipefromtext/json")
    fun findRecipeFromText(
        @Query("input") input: String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("fields") fields: String = "name,rating",
        @Query("key") apiKey: String

    ): Call<RecipeResponse>
}