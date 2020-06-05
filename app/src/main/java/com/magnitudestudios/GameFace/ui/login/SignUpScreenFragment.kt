package com.magnitudestudios.GameFace.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.magnitudestudios.GameFace.callbacks.UserLoginListener
import com.magnitudestudios.GameFace.databinding.FragmentSignupBinding
import com.magnitudestudios.GameFace.pojo.User
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpScreenFragment : Fragment(), View.OnClickListener {
    private var listener: UserLoginListener? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var binding: FragmentSignupBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButtonSignup.setOnClickListener(this)
        binding.signupCloseBtn.setOnClickListener(this)

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
        if (binding.signupPasswordInput.text.toString() != binding.signupCPasswordInput.text.toString()) {
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
                        with(mAuth?.currentUser!!) {
                            myRef.child(uid).setValue(User(uid, email, username, "NO URL", "NO NAME"))
                        }
                        listener!!.signedInUser()
                    } else {
                        Log.e(TAG, "onCompleteSignUp: FAILURE")
                        Toast.makeText(context, "Sign Up Failed", Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.signupButtonSignup -> if (validateDetails()) {
                signUpUser(binding.signupUsernameInput.text.toString(), binding.signupEmailInput.text.toString(), binding.signupPasswordInput.text.toString())
            }
            binding.signupCloseBtn -> findNavController().popBackStack()
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