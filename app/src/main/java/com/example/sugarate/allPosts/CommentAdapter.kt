package com.example.sugarate.allPosts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarate.databinding.FragmentCommentItemBinding
import com.example.sugarate.posts.FirebasePost
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentAdapter : ListAdapter<Pair<String, FirebasePost>, CommentAdapter.CommentViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = FragmentCommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position).second)
    }

    class CommentViewHolder(private val binding: FragmentCommentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(firebasePost: FirebasePost) {
            // Set the comment text and photo
            binding.commentTextView.text = firebasePost.comment
            firebasePost.photo?.let {
                if (it.isNotEmpty()) {
                    Picasso.get().load(it).into(binding.commentPhotoImageView)
                }
            }

            // Replace the userId with fullName by querying the "Users" node
            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
            firebasePost.userId?.let { userId ->
                usersRef.orderByChild("userId").equalTo(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children) {
                                // Assuming the structure of your user's data is as in the "Users" node
                                val fullName = userSnapshot.child("fullName").getValue(String::class.java)
                                binding.commentUserIdTextView.text = fullName ?: "Unknown User"
                                break // Assuming userId is unique, break after finding the first match
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                            binding.commentUserIdTextView.text = "Error loading user"
                        }
                    })
            }
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pair<String, FirebasePost>>() {
            override fun areItemsTheSame(oldItem: Pair<String, FirebasePost>, newItem: Pair<String, FirebasePost>): Boolean {
                return oldItem.first == newItem.first // Compare the Firebase keys
            }

            override fun areContentsTheSame(oldItem: Pair<String, FirebasePost>, newItem: Pair<String, FirebasePost>): Boolean {
                return oldItem.second == newItem.second // Compare the contents of the FirebasePost objects
            }
        }
    }
}
