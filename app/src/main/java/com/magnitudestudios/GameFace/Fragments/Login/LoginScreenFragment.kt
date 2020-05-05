package com.magnitudestudios.GameFace.Fragments.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.magnitudestudios.GameFace.Interfaces.UserLoginListener
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentLoginBinding

class LoginScreenFragment : Fragment(), View.OnClickListener {
    private var listener: UserLoginListener? = null
    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oAuth_client_ID))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        mAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginBtnSignup.setOnClickListener(this)
        binding.loginSignButton.setOnClickListener(this)
        binding.loginCardSigninwithgoogle.setOnClickListener(this)
        binding.forgotPassword.setOnClickListener(this)
        binding.loginCloseBtn.setOnClickListener(this)
    }

    private fun signInUser() {
        mAuth!!.signInWithEmailAndPassword(binding.loginEmailInput.text.toString(), binding.loginPasswordInput.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        listener!!.signedInUser()
                    } else {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun validateEmail(email: String): Boolean {
        return !email.isEmpty() && email.contains("@") && email.contains(".")
    }

    private fun validate(): Boolean {
        var valid = true
        val email = binding.loginEmailInput.text.toString()
        val password = binding.loginPasswordInput.text.toString()
        if (!validateEmail(email)) {
            binding.loginEmailInput.error = "Please enter a valid email address"
            valid = false
        }
        if (password.isEmpty()) {
            binding.loginPasswordInput.error = "Please enter your password"
            valid = false
        }
        return valid
    }

    private fun onClickSignWithGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, GOOGLE_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_RESULT) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginFragment", "Google sign in failed", e)
                Toast.makeText(context, "Sign in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener!!.signedInUser()
            } else {
                Toast.makeText(context, "Sign up failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendForgotPassword() {
        val emailAddress = binding.loginEmailInput.text.toString()
        if (validateEmail(emailAddress)) {
            mAuth?.sendPasswordResetEmail(emailAddress)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset email sent to $emailAddress ", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error occurred. Email not sent", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            binding.loginEmailInput.error = "Please enter a valid email!"
        }
    }


    override fun onClick(v: View) {
        when (v) {
            binding.loginSignButton -> if (validate()) {
                signInUser()
            }
            binding.loginCardSigninwithgoogle -> onClickSignWithGoogle()
            binding.forgotPassword -> sendForgotPassword()
            binding.loginCloseBtn -> findNavController().popBackStack()
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
        private const val TAG = "LoginScreenFragment"
        private const val GOOGLE_RESULT = 101
    }
}