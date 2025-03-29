package com.example.sugarate.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sugarate.login.UserCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {

    private val _signUpResult = MutableLiveData<String>() //to hold the result of the sign-up operation
    val signUpResult: LiveData<String> get() = _signUpResult // to allow fragments or activities to observe changes to the sign-up result

    private lateinit var auth: FirebaseAuth
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun signUpUser(credentials: UserCredentials, userProperties: UserProperties) {
        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    val userRef = firebaseDatabase.reference.child("Users").child(userId)
                    val user = createUserMap(userId,userProperties)

                    userRef.setValue(user)
                        .addOnSuccessListener {
                            _signUpResult.value = "Success"
                        }
                        .addOnFailureListener { e ->
                            _signUpResult.value = "Failed to create user: ${e.message}"
                        }
                } else {
                    _signUpResult.value = "Failed to create user: User ID is null"
                }
            }
            .addOnFailureListener { e ->
                _signUpResult.value = "Error: ${e.message}"
            }
    }

    private fun createUserMap(userId: String,userProperties: UserProperties): Map<String, Any> {
        val userMap = HashMap<String, Any>()
        userMap["fullName"] = userProperties.fullName
        userMap["userId"] = userId
        return userMap
    }
}
