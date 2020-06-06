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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.UserLoginListener
import com.magnitudestudios.GameFace.databinding.FragmentSignupBinding
import com.magnitudestudios.GameFace.network.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.User
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpScreenFragment : Fragment(), View.OnClickListener {
    private var listener: UserLoginListener? = null

    private lateinit var binding: FragmentSignupBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
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
            binding.signupUsernameInput.error = getString(R.string.username_length)
        }
        if (!binding.signupEmailInput.text.toString().contains("@") || !binding.signupEmailInput.text.toString().contains(".")) {
            binding.signupEmailInput.error = getString(R.string.enter_valid_email)
        }
        if (binding.signupPasswordInput.text.toString().length <= 5) {
            valid = false
            binding.signupPasswordInput.error = getString(R.string.pwd_length)
        }
        if (binding.signupPasswordInput.text.toString() != binding.signupCPasswordInput.text.toString()) {
            valid = false
            signup_cPasswordInput.error = getString(R.string.pwd_must_match)
        }
        return valid
    }

    private fun signUpUser(username: String, email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onCompleteSignUp: SUCCESS")

                        FirebaseHelper.createUser(User(Firebase.auth.uid!!, email, username, "NO URL", "NO NAME"))
                        listener!!.signedInUser()
                    }
                    else {
                        Log.e(TAG, "onCompleteSignUp: " + task.exception.toString())
                        Toast.makeText(context, getString(R.string.sign_up_failed), Toast.LENGTH_LONG).show()
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