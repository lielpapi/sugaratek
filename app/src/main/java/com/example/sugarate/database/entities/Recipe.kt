package com.example.sugarate.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val materials: String,
    val preparation: String,
    val dishPhoto: String,
    val calories: Int
)