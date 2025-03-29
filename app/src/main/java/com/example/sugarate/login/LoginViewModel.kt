package com.example.sugarate.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Pair<HashMap<String, Any>, String>>() //hashmap-user data,string-user ID
    val loginResult: LiveData<Pair<HashMap<String, Any>, String>> get() = _loginResult // public property, allowing other classes (like fragments or activities) to observe changes to the login result

    private lateinit var auth: FirebaseAuth
    private val firebaseDatabase = FirebaseDatabase.getInstance()


    fun loginUser(credentials: UserCredentials) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                user?.let { firebaseUser ->
                    val userId = firebaseUser.uid
                    // Retrieve user data from Realtime Database
                    firebaseDatabase.reference.child("Users").child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userData = dataSnapshot.value as HashMap<String, Any>?
                                userData?.let {
                                    // Emit login result with user data and email
                                    _loginResult.value = Pair(userData, firebaseUser.email ?: "")
                                } ?: run {
                                    // User data is null
                                    _loginResult.value = Pair(hashMapOf(), "")
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle database error
                                _loginResult.value = Pair(hashMapOf(), "")
                            }
                        })
                } ?: run {
                    // Auth user is null
                    _loginResult.value = Pair(hashMapOf(), "")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to sign in
                _loginResult.value = Pair(hashMapOf(), "")
            }
    }


}
