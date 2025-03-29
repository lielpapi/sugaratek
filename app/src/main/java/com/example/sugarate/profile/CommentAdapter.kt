package com.example.sugarate.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarate.R
import com.squareup.picasso.Picasso


// RecyclerView adapter
//responsible for managing the items in the RecyclerView
class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    // ViewHolder class
    // Define interfaces for click listeners
    interface EditClickListener {
        fun onEditClick(position: Int)
    }

    interface DeleteClickListener {
        fun onDeleteClick(position: Int)
    }
    // Define editClickListener and deleteClickListener variables
    var editClickListener: EditClickListener? = null
    var deleteClickListener: DeleteClickListener? = null

    //nested class that represents the view for each item in the RecyclerView
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Bind data to views
        fun bind(comment: Comment) {
            // Bind comment data to views in the layout
            Log.d("commentProfileCheck","comment id: "+ comment.commentId)
            Log.d("commentProfileCheck","comment text: "+ comment.comment)
            Log.d("commentProfileCheck","comment dishName: "+ comment.dishName)
            itemView.findViewById<TextView>(R.id.DishNameTextView).text = comment.dishName
            itemView.findViewById<TextView>(R.id.commentTextView).text = comment.comment
            // Load image using Picasso or Glide
            Picasso.get().load(comment.photo).into(itemView.findViewById<ImageView>(R.id.photoImageView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        if (comments.isEmpty()) {
            // Handle the case where there are no comments to display
            holder.itemView.findViewById<TextView>(R.id.commentTextView).text = "You haven't commented yet"
            holder.itemView.findViewById<ImageView>(R.id.photoImageView).setImageResource(R.drawable.placeholder_image) // Set a placeholder image or hide the ImageView
            holder.itemView.findViewById<ImageButton>(R.id.editButton).visibility = View.GONE // Hide edit button
            holder.itemView.findViewById<ImageButton>(R.id.deleteButton).visibility = View.GONE // Hide delete button
        } else {
            // Bind comment data if comments list is not empty
            holder.bind(comments[position])
            // Set click listeners for edit and delete buttons
            holder.itemView.findViewById<ImageButton>(R.id.editButton).setOnClickListener {
                editClickListener?.onEditClick(position)
            }

            holder.itemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
                deleteClickListener?.onDeleteClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        // If comments list is empty, return 1 for the placeholder
        // Otherwise, return the size of the comments list
        return if (comments.isEmpty()) 1 else comments.size
    }
}

