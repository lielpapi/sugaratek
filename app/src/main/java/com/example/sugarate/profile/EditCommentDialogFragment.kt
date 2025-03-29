package com.example.sugarate.profile


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.sugarate.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

class EditCommentDialogFragment : DialogFragment() {
    private lateinit var imageViewCommentPhoto: ImageView
    private lateinit var editTextCommentText: EditText
    private lateinit var pickImageContract: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.fragment_edit_comment_dialog, null)

        editTextCommentText = view.findViewById(R.id.editTextComment)
        imageViewCommentPhoto = view.findViewById(R.id.imageViewCommentPhoto)
        val buttonSelectCommentPhoto: Button = view.findViewById(R.id.buttonSelectCommentPhoto)
        // Fetch comment ID from arguments
        val commentId: String? = arguments?.getString("commentId")

        // Fetch comment data from existing comments in ViewModel
        commentId?.let { id ->
            val comment = viewModel.userComments.value?.find { it.commentId == id }
            comment?.let {
                editTextCommentText.setText(comment.comment)
                Picasso.get().load(comment.photo).into(imageViewCommentPhoto)
            }
        }
        // Initialize pickImageContract
        pickImageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let {
                    // Load and display the selected image in the ImageView using Picasso
                    Picasso.get().load(selectedImageUri).into(imageViewCommentPhoto)
                }
            }
        }

        // Set click listener for selecting comment photo
        buttonSelectCommentPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            pickImageContract.launch(intent)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_comment_dialog_title)
            .setView(view)
            .setPositiveButton(R.string.save) { dialog, which ->
                // Handle save button click
                val newText = editTextCommentText.text.toString()
                // Update comment text and photo if changed
                val comment = viewModel.userComments.value?.find { it.commentId == commentId }
                comment?.let {
                    if (newText != it.comment || selectedImageUri != null) {
                        var updatedComment = it.copy(comment = newText)
                        selectedImageUri?.let { uri ->
                            // If a new photo is selected, update the comment's photo URL
                            uploadImageAndUpdateComment(updatedComment, uri)
                        }?:run {
                            // Update the comment in the ViewModel
                            viewModel.updateComment(updatedComment)
                        }
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
                // Handle cancel button click
            }
            .create()
    }

    private fun uploadImageAndUpdateComment(comment: Comment, imageUri: Uri) {
        // Upload image to Firebase Storage
        viewModel.uploadImageToFirebaseStorage(imageUri) { photoUrl ->
            // Update comment photo URL with the new photo URL
            val updatedComment = comment.copy(photo = photoUrl)
            // Update the comment in the ViewModel
            viewModel.updateComment(updatedComment)
        }
    }
}
