package com.example.sugarate.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sugarate.databinding.FragmentPostBinding
import com.example.sugarate.posts.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            binding.imageViewUploadedPhoto.setImageURI(uri)
            selectedImageUri = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dishName = arguments?.getString("dishName") ?: "Unknown"
        Log.d("dishName", "$dishName ! ")

        // ✨ לא צריך הרשאות – פשוט לפתוח את הבוחר
        binding.buttonChoosePhoto.setOnClickListener {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }

        binding.buttonSubmitComment.setOnClickListener {
            val commentText = binding.inputComment.text.toString()
            val userId = getCurrentUserId()

            selectedImageUri?.let { uri ->
                if (userId != null && commentText.isNotEmpty()) {
                    uploadImageToFirebaseStorage(uri) { photoUrl ->
                        viewModel.createPost(userId, commentText, photoUrl, dishName,
                            onSuccess = {
                                findNavController().navigateUp()
                            },
                            onFailure = { e ->
                                Log.e("PostFragment", "Failed to create post", e)
                                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Please write a comment", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Please share your dish picture", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.buttonSubmitComment.setOnClickListener {
            val commentText = binding.inputComment.text.toString()
            val userId = getCurrentUserId()

            selectedImageUri?.let { uri ->
                if (userId != null && commentText.isNotEmpty()) {
                    uploadImageToFirebaseStorage(uri) { photoUrl ->
                        viewModel.createPost(userId, commentText, photoUrl, dishName,
                            onSuccess = {
                                findNavController().navigateUp()
                            },
                            onFailure = { e ->
                                Log.e("PostFragment", "Failed to create post", e)
                                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Please write a comment", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "Please share your dish picture", Toast.LENGTH_SHORT).show()
            }
        }

        binding.goBackIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imagePickerLauncher.launch(arrayOf("image/*"))
        } else {
            Toast.makeText(requireContext(), "Permission denied to access media", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                task.exception?.message?.let {
                    Toast.makeText(requireContext(), "Upload failed: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
