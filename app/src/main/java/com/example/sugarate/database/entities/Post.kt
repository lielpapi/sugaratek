package com.example.sugarate.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val userId: String,
    val comment: String,
    val photo: String,
    val dishName: String,
)