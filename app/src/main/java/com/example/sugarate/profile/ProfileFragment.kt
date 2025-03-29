package com.example.sugarate.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sugarate.R
import com.example.sugarate.Welcome
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException


class ProfileFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    CommentAdapter.EditClickListener, CommentAdapter.DeleteClickListener {

    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var profileImageView: CircleImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation : Location
    private var currentName: String? = null
    private var currentProfilePicUrl: String? = null

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(
            R.layout.fragment_profile, container, false
        )
        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)
        editProfileButton = view.findViewById(R.id.editProfileButton)
        commentRecyclerView = view.findViewById(R.id.userCommentRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfilePhoto()
        observeUserData()
        observeUserComments()
        auth = FirebaseAuth.getInstance()
        logoutButton.setOnClickListener {
            logoutUser()
        }

        editProfileButton.setOnClickListener {
            editProfile()
        }

        // Fetch and display profile photo
        val defaultPhotoResourceId = R.drawable.profile_photo_placeholder
        val defaultPhotoUri = Uri.parse("android.resource://${requireContext().packageName}/$defaultPhotoResourceId")

        viewModel.fetchProfilePhoto(defaultPhotoUri)
        viewModel.fetchUserName()
        viewModel.fetchUserEmail()
        viewModel.fetchUserComments()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this@ProfileFragment)
        setupMap()
    }
    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                addMarkers()
            } else {
                Toast.makeText(requireContext(), "Could not get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarkers() {
        // Add a marker for Algorithmim
        val location1 = LatLng(31.990604,34.774915)
        val marker = googleMap.addMarker(MarkerOptions().position(location1).title("Hava's soup factory, Rishon Lezion"))
        val location2 = LatLng(31.9733044,34.7760967)
        val marker2 = googleMap.addMarker(MarkerOptions().position(location2).title("Hava's desert paradise, Rishon Lezion"))

        // Move the camera to the Rishon LeZion marker
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 12f))
    }

    private fun observeUserComments() {
        viewModel.userComments.observe(viewLifecycleOwner) { comments ->
            // Update RecyclerView with the list of comments
            commentRecyclerView.adapter = CommentAdapter(comments).apply {
                editClickListener = this@ProfileFragment
                deleteClickListener = this@ProfileFragment
            }
        }
    }

    private fun observeProfilePhoto() {
        viewModel.profilePhotoUrl.observe(viewLifecycleOwner) { photoUrl ->
            Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.profile_photo_placeholder) // Placeholder image while loading
                .error(R.drawable.profile_photo_placeholder) // Image to show in case of error
                .into(profileImageView)
        }
    }

    private fun observeUserData() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            Log.d("TAG", "observeUserData: $name")
            // Update UI with user's name
            nameTextView.text = name
            currentName = name
        }

        val userEmail = viewModel.fetchUserEmail()
        // Update UI with user's email
        emailTextView.text = userEmail
    }

    private fun logoutUser() {
        auth.signOut()
        // navigate the user to welcome activity
        val intent = Intent(requireContext(), Welcome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun editProfile() {
        val dialogFragment = EditProfileDialogFragment().apply {
            arguments = Bundle().apply {
                putString("currentName", currentName)
                putString("currentProfilePicUrl", currentProfilePicUrl)
            }
        }
        dialogFragment.show(parentFragmentManager, "EditProfileDialogFragment")
    }

    override fun onEditClick(position: Int) {
        // Retrieve the comment at the given position
        val comment = viewModel.userComments.value?.get(position)
        // Open a dialog fragment to edit the comment
        comment?.let {
            val args = Bundle().apply {
                putString("commentId", it.commentId)
            }

            val editCommentDialogFragment = EditCommentDialogFragment().apply {
                arguments = args
            }

            editCommentDialogFragment.show(parentFragmentManager, "EditCommentDialogFragment")
        }
    }

    override fun onDeleteClick(position: Int) {
        // Retrieve the comment at the given position
        val comment = viewModel.userComments.value?.get(position)

        //delete the comment from the database
        comment?.let {
            viewModel.deleteComment(comment.commentId)
        }
    }


    private fun fetchFullAddress(marker: Marker) {
        val location = marker.position
        // Call your method to fetch the full address based on the marker's position
        val fullAddress = fetchFullAddressFromGeocodingService(location)
        // Set the full address as the snippet of the marker
        marker.snippet = fullAddress
    }
    fun fetchFullAddressFromGeocodingService(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext())
        var fullAddress = ""
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressParts = mutableListOf<String>()
                    for (i in 0..address.maxAddressLineIndex) {
                        addressParts.add(address.getAddressLine(i))
                    }
                    fullAddress = addressParts.joinToString(", ")
                }
            }
        } catch (e: IOException) {
            Log.e("ProfileFragment", "Error fetching address: ${e.message}")
        }
        return fullAddress
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Fetch the full address for the clicked marker
        fetchFullAddress(marker)
        // Show the info window for the clicked marker
        marker.showInfoWindow()
        // Return true to indicate that we have handled the click event
        return true
    }
}


