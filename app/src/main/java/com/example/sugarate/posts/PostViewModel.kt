package com.example.sugarate.posts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sugarate.database.DatabaseInstance
import com.example.sugarate.database.entities.Post
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun createPost(
        userId: String,
        comment: String,
        photoUrl: String,
        dishName: String,
        onSuccess: () -> Unit,  // onSuccess lambda parameter
        onFailure: (Exception) -> Unit  // onFailure lambda parameter
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Create the Post object
                val newPost = Post(
                    userId = userId,
                    comment = comment,
                    photo = photoUrl,
                    dishName = dishName
                )

                // Insert the new post into the Room database
                val db = DatabaseInstance.getDatabase(getApplication<Application>())
                val postDao = db.postDao()
                Log.d("newPost",newPost.dishName)
                postDao.insertPost(newPost)

                // Insert the new post into Firebase and wait for completion
                val postsRef = firebaseDatabase.getReference("posts")
                val newPostRef = postsRef.push()
                newPostRef.setValue(newPost).await()

                // If successful, invoke onSuccess callback
                launch(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                // If there's an error, invoke onFailure callback
                launch(Dispatchers.Main) { onFailure(e) }
            }
        }
    }
}
