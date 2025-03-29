package com.example.sugarate.profile

import android.net.Uri
import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class ProfileViewModel : ViewModel() {

    private val _profilePhotoUrl = MutableLiveData<String>()
    val profilePhotoUrl: LiveData<String> get() = _profilePhotoUrl

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userComments = MutableLiveData<List<Comment>>()
    val userComments: LiveData<List<Comment>> get() = _userComments

    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun fetchUserComments() {
       // Log.d("TAG","hello")
        currentUser?.uid?.let { userId ->
            val commentsRef = database.getReference("posts").orderByChild("userId").equalTo(userId)
            commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentsList = mutableListOf<Comment>()
                    for (commentSnapshot in snapshot.children) {
                        val commentId = commentSnapshot.key ?: ""
                        val commentText = commentSnapshot.child("comment").getValue(String::class.java) ?: ""
                        val dishName = commentSnapshot.child("dishName").getValue(String::class.java) ?: ""
                        val photoUrl = commentSnapshot.child("photo").getValue(String::class.java) ?: ""
                        val comment = Comment(commentId,commentText, photoUrl, dishName)
                        commentsList.add(comment)
                    }
                    Log.d("TAG","commentsList:$commentsList")
                    _userComments.value = commentsList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }

    fun fetchProfilePhoto(defaultPhotoUri: Uri) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val photoRef = storage.reference.child("profile_photos/$userId.jpg")

            // Check if the profile photo exists
            photoRef.metadata.addOnSuccessListener { metadata ->
                if (metadata != null && metadata.sizeBytes > 0) {
                    // Profile photo exists, fetch its download URL
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        _profilePhotoUrl.value = uri.toString()
                    }.addOnFailureListener {
                        // Handle failure to fetch download URL
                        _profilePhotoUrl.value = defaultPhotoUri.toString()
                    }
                } else {
                    // Profile photo doesn't exist, use default photo
                    _profilePhotoUrl.value = defaultPhotoUri.toString()
                }
            }.addOnFailureListener {
                // Handle failure to retrieve metadata
                _profilePhotoUrl.value = defaultPhotoUri.toString()
            }
        } else {
            // Handle the case when currentUser is null (user not logged in)
            println("currentUser is null")
        }
    }


    fun fetchUserName() {
        currentUser?.uid?.let { userId ->
            val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("fullName").value.toString()
                    // Update UI with the retrieved name
                    _userName.value = name
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error

                }
            })
        }
    }
    fun fetchUserEmail(): String? {
        return currentUser?.email
    }
    fun uploadProfileImage(userMetaData: UserMetaData, imageUri: Uri) {
        val userId = currentUser?.uid
        userId?.let {
            val photoRef = storage.reference.child("profile_photos/$userId.jpg")
            photoRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Photo upload successful, fetch its download URL
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        // Update user's profile photo URL in Firebase Realtime Database
                        val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                        databaseRef.child("profilePhoto").setValue(uri.toString())
                            .addOnSuccessListener {
                                // Profile photo URL updated successfully
                                _profilePhotoUrl.value = uri.toString()
                            }.addOnFailureListener {
                                // Handle failure to update profile photo URL in database


                            }
                    }
                }
                .addOnFailureListener {
                    // Handle failure to upload photo
                    Log.d("TAG", "Failed to upload profile photo: ${it.message}")
                }
        }
    }

    fun updateUserName(userMetaData: UserMetaData, newName: String) {
        val userId = currentUser?.uid
        userId?.let {
            val databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            databaseRef.child("fullName").setValue(newName)
                .addOnSuccessListener {
                    // User name updated successfully
                    Log.d("TAG", "_userName: ${_userName.value}")
                    _userName.value = newName
                    Log.d("TAG", "_userName: ${_userName.value}")
                }
                .addOnFailureListener {
                    // Handle failure to update user name
                }
        }
    }

    fun deleteComment(commentId: String) {
        // Get a reference to the comments node in the database
        val commentsRef = database.getReference("posts")

        // Delete the comment with the specified commentId
        commentsRef.child(commentId).removeValue()
            .addOnSuccessListener {
                // Comment deleted successfully
                Log.d("ProfileViewModel", "Comment deleted successfully")
                // Remove the deleted comment from the userComments LiveData
                val updatedCommentsList = _userComments.value.orEmpty().toMutableList()
                updatedCommentsList.removeAll { it.commentId == commentId }
                _userComments.value = updatedCommentsList
            }
            .addOnFailureListener { exception ->
                // Failed to delete comment
                Log.e("ProfileViewModel", "Error deleting comment", exception)
            }

    }

    fun updateComment(updatedComment: Comment) {
        // Get a reference to the comments node in the database
        val commentsRef = database.getReference("posts")

        // Create a map to hold the updated properties
        val commentUpdates = HashMap<String, Any>()

        // Update the specific properties you want to change
        commentUpdates["comment"] = updatedComment.comment
        commentUpdates["photo"] = updatedComment.photo
        // Update the comment with the specified commentId
        commentsRef.child(updatedComment.commentId).updateChildren(commentUpdates)
            .addOnSuccessListener {
                // Comment updated successfully
                Log.d("ProfileViewModel", "Comment updated successfully")

                // Update the comment in the userComments LiveData
                val updatedCommentsList = _userComments.value.orEmpty().toMutableList()
                val index = updatedCommentsList.indexOfFirst { it.commentId == updatedComment.commentId }
                if (index != -1) {
                    updatedCommentsList[index] = updatedComment
                    _userComments.value = updatedCommentsList
                }
            }
            .addOnFailureListener { exception ->
                // Failed to update comment
                Log.e("ProfileViewModel", "Error updating comment", exception)
            }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}_${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                // Handle the error
                task.exception?.message?.let {
                    // Show error message to the user
                }
            }
        }
    }


}
