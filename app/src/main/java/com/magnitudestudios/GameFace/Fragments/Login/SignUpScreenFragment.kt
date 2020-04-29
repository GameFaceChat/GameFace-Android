package com.magnitudestudios.GameFace.Fragments.Login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.magnitudestudios.GameFace.Interfaces.UserLoginListener
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentSignupBinding
import com.magnitudestudios.GameFace.pojo.User
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpScreenFragment : Fragment(), View.OnClickListener {
    private var listener: UserLoginListener? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var binding: FragmentSignupBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        //Buttons
        binding.signupGottologin.setOnClickListener(this)
        binding.signupButtonSignup.setOnClickListener(this)

        //EditTexts
        mAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    private fun validateDetails(): Boolean {
        var valid = true
        if (binding.signupUsernameInput.text.toString().length <= 5) {
            valid = false
            binding.signupUsernameInput.error = "Username must be more than 5 characters"
        }
        if (!binding.signupEmailInput.text.toString().contains("@") || !binding.signupEmailInput.text.toString().contains(".")) {
            binding.signupEmailInput.error = "Please enter a valid email"
        }
        if (binding.signupPasswordInput.text.toString().length <= 6) {
            valid = false
            binding.signupPasswordInput.error = "Password must be more than 6 characters in length"
        }
        if (binding.signupPasswordInput.text.toString() != binding.signupCPasswordInput!!.text.toString()) {
            valid = false
            signup_cPasswordInput.error = "Passwords must match"
        }
        return valid
    }

    private fun signUpUser(username: String, email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onCompleteSignUp: SUCCESS")
                        val database = FirebaseDatabase.getInstance()
                        val myRef = database.getReference("users")
                        myRef.child(mAuth!!.currentUser!!.uid).setValue(User(email, username))
                        listener!!.signedInUser()
                    } else {
                        Log.e(TAG, "onCompleteSignUp: FAILURE")
                        Toast.makeText(context, "Sign Up Failed", Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signup_gottologin -> findNavController().navigate(R.id.action_signUpScreenFragment_to_loginScreenFragment)
            R.id.signup_button_signup -> if (validateDetails()) {
                signUpUser(binding.signupUsernameInput.text.toString(), binding.signupEmailInput.text.toString(), binding.signupPasswordInput.text.toString())
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as UserLoginListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context Must implement UserLoginListener")
        }
    }

    companion object {
        private const val TAG = "SignUpScreenFragment"
    }
}