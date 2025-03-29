package com.example.sugarate.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sugarate.database.entities.Post

@Dao
interface PostDao {
    @Insert
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM posts")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): Post?

    @Query("SELECT * FROM posts WHERE userId = :userId ")
    fun getPostsByUserId(userId: String): LiveData<List<Post>>
}