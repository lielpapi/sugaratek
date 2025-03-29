package com.example.sugarate.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.sugarate.MainActivity
import com.example.sugarate.R
import com.example.sugarate.login.LoginActivity
import com.example.sugarate.login.UserCredentials


class SignUpFragment : Fragment() {
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var fullNameInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var progressBarSignUp: ProgressBar
    private lateinit var loginLink:TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
            R.layout.fragment_sign_up, container, false
        )
        emailInput = view.findViewById(R.id.emailInput)
        passwordInput = view.findViewById(R.id.passwordInput)
        fullNameInput = view.findViewById(R.id.fullNameInput)
        signUpButton = view.findViewById(R.id.signUpButton)
        progressBarSignUp = view.findViewById(R.id.progress_bar)
        loginLink = view.findViewById<TextView>(R.id.login_link)

        handleSignUpClick(signUpButton)
        handleLoginClick(loginLink)
        observeSignUpResult()
        return view
    }

    private fun handleLoginClick(loginLink: TextView) {
        loginLink.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeSignUpResult() {
        signUpViewModel.signUpResult.observe(viewLifecycleOwner) { result: String ->
            if (result == "Success") {
//              findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
               val intent = Intent(requireActivity(), MainActivity::class.java)
               startActivity(intent)
            } else {
                resetParameters()
//                manageViews(
//                    emailInput, passwordInput, fullNameInput,
//                    signUpButton, mode = "VISIBLE"
//                )
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
            }
            progressBarSignUp.visibility = View.GONE
        }
    }

    private fun handleSignUpClick(signUpButton: Button) {
        signUpButton.setOnClickListener {
            val credentials = UserCredentials(
                emailInput.text.toString(), passwordInput.text.toString()
            )
            val userProperties = UserProperties(
                fullNameInput.text.toString()
            )
            if (checkCredentials(credentials, emailInput, passwordInput) &&
                checkUserProperties(userProperties, fullNameInput)
            ) {
//                manageViews(
//                    emailInput, passwordInput, fullNameInput,
//                     signUpButton, mode = "GONE"
//                )
                progressBarSignUp.visibility = View.VISIBLE
                signUpViewModel.signUpUser(credentials, userProperties)
            }
        }
    }

    private fun resetParameters() {
        emailInput.text.clear()
        passwordInput.text.clear()
        fullNameInput.text.clear()
    }
    private fun isString(value: String): Boolean {
        return value.all { it.isLetter() || it.isWhitespace()}
    }

    private fun checkCredentials(
        credentials: UserCredentials, emailInput: EditText,
        passwordInput:EditText): Boolean{
        if (credentials.email.isEmpty()){
            emailInput.error = "Enter a email"
        }
        else if (credentials.password.isEmpty()){
            passwordInput.error = "Enter a password"
        }
        else if (credentials.password.length <=6) {
            passwordInput.error = "Password need to more than 6 characters long"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()){
            emailInput.error = "Enter valid email format"
        }
        else{ return true }
        return false
    }

    private fun checkUserProperties(
        userProperties: UserProperties,
        fullNameInput: EditText
    ): Boolean {
        if (userProperties.fullName.isEmpty() || !isString(userProperties.fullName)) {
            fullNameInput.error = "Enter valid full name"
        }
        else {
            return true
        }
        return false
    }

    //can be in utils- globalFunctions
    private fun manageViews(vararg views: View, mode: String) {
        for (view in views) {
            if (mode == "GONE") { view.visibility = View.GONE }
            else{ view.visibility = View.VISIBLE }
        }
    }


}