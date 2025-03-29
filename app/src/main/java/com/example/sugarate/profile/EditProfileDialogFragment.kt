package com.example.sugarate.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sugarate.R
import com.example.sugarate.shared.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

class EditProfileDialogFragment : DialogFragment() {
    private lateinit var imageViewProfilePic: ImageView
    private lateinit var pickImageContract: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_edit_profile_dialog, null)

        val editTextProfileName = view.findViewById<EditText>(R.id.editTextProfileName)
        imageViewProfilePic = view.findViewById<ImageView>(R.id.imageViewProfilePic)
        val buttonSelectProfilePic = view.findViewById<Button>(R.id.buttonSelectProfilePic)
        val currentName = arguments?.getString("currentName")
        // Initialize pickImageContract
        pickImageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let {
                    // Load and display the selected image in the ImageView using Picasso
                    Picasso.get().load(selectedImageUri).into(imageViewProfilePic)
                }
            }
        }

        // Pre-fill the EditText field with the current name
        editTextProfileName.setText(currentName)

        // Fetch and display the profile photo from ProfileViewModel
        val defaultPhotoResourceId = R.drawable.profile_photo_placeholder
        val defaultPhotoUri = Uri.parse("android.resource://${requireContext().packageName}/$defaultPhotoResourceId")
        viewModel.fetchProfilePhoto(defaultPhotoUri)
        viewModel.profilePhotoUrl.observe(this) { photoUrl ->
            Picasso.get().load(photoUrl)
                .placeholder(R.drawable.profile_photo_placeholder)
                .error(R.drawable.profile_photo_placeholder)
                .into(imageViewProfilePic)
        }
        // Set click listener for selecting profile picture
        buttonSelectProfilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            pickImageContract.launch(intent)
        }

        val positiveButtonColor = "#4dbde0"
        val negativeButtonColor = "#4dbde0"

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_profile_dialog_title)
            .setView(view) // Set custom layout as dialog content view
            .setPositiveButton(R.string.save) { dialog, which ->
                // Handle save button click
                val newName = editTextProfileName.text.toString()
                Log.d("TAG", "selectedImageUri: $selectedImageUri")
                // // Update profile with newName if it's different from the current name or if a new profile picture has been selected
                if (newName != sharedViewModel.userMetaData.fullName || selectedImageUri != null) {
                    if (newName != sharedViewModel.userMetaData.fullName) {
                        Log.d("TAG", "sharedViewModel.userMetaData.fullName: ${sharedViewModel.userMetaData.fullName}")
                        viewModel.updateUserName(sharedViewModel.userMetaData, newName)
                        sharedViewModel.userMetaData.fullName = newName
                    }
                    selectedImageUri?.let { uri ->
                        viewModel.uploadProfileImage(sharedViewModel.userMetaData, uri)
                        sharedViewModel.userMetaData.profilePhoto = uri.toString()
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
                // Handle cancel button click
            }
            .create()

        // Set text color for positive button
        dialog.setOnShowListener {
            dialog.getButton(Dialog.BUTTON_POSITIVE)?.setTextColor(android.graphics.Color.parseColor(positiveButtonColor))
            dialog.getButton(Dialog.BUTTON_NEGATIVE)?.setTextColor(android.graphics.Color.parseColor(negativeButtonColor))
        }

        return dialog
    }
}
