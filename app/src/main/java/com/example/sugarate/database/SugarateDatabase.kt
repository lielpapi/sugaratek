package com.example.sugarate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sugarate.database.dao.RecipeDao
import com.example.sugarate.database.dao.PostDao
import com.example.sugarate.database.dao.UserDao
import com.example.sugarate.database.entities.Recipe
import com.example.sugarate.database.entities.Post
import com.example.sugarate.database.entities.User

@Database(entities = [User::class, Recipe::class, Post::class], version = 9)
abstract class SugarateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun postDao(): PostDao
}