/*
 * Copyright (c) 2021 -Srihari Vishnu - All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.magnitudestudios.GameFace.ui.login

import android.net.Uri
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ServerValue
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    val authenticated = MutableLiveData<Resource<Boolean>>()

    val usernameExists = MutableLiveData<Resource<Boolean>>()

    val profilePicUri = MutableLiveData<Uri>()

    init {
        if (Firebase.auth.currentUser != null) {
            authenticated.value = Resource.success(true)
            Log.e("ALREADY", "LOGGED IN with " + Firebase.auth.currentUser!!.email)
        }
    }

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun resetUserStatus() {
        authenticated.postValue(Resource.nothing(false))
    }

    fun signUpUserWithEmail(email: String, password: String): LiveData<Resource<Boolean>> {
        authenticated.value = Resource.loading(false)
        return liveData(Dispatchers.IO) {
            emit(Resource.loading(false))
            try {
                Firebase.auth.createUserWithEmailAndPassword(email, password).await()
                UserRepository.createUser(User(Firebase.auth.uid!!, ServerValue.TIMESTAMP, hashMapOf(
                        Pair(UserRepository.getDeviceToken(), true)
                ))).await()
                emit(Resource.success(true))

            } catch (e: FirebaseException) {
                Log.e("LoginViewModel: ", "Error when creating user", e)
                emit(Resource(Status.ERROR, false, e.localizedMessage))
            }
            authenticated.postValue(Resource.nothing(false))
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?): LiveData<Resource<Boolean>> {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        return liveData(Dispatchers.IO) {
            try {
                Firebase.auth.signInWithCredential(credential).await()
                //Login (So send back true)
                if (UserRepository.getUserProfileByUID(Firebase.auth.currentUser?.uid!!) != null) {
                    emit(Resource.success(true))
                    authenticated.postValue(Resource.success(true))
                }
                //New User (So send back false)
                else {
                    UserRepository.createUser(User(Firebase.auth.uid!!, ServerValue.TIMESTAMP, hashMapOf(
                            Pair(UserRepository.getDeviceToken(), true)
                    ))).await()
                    emit(Resource.success(false))
                }
            } catch (e: FirebaseException) {
                emit(Resource(Status.ERROR, false, e.localizedMessage))
            }
        }
    }


    fun signInWithEmail(email: String, password: String) {
        authenticated.value = Resource.loading(false)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                authenticated.postValue(Resource.success(true))
            } catch (e: FirebaseException) {
                Log.e("LoginViewModel: ", "Error when signing in user", e)
                authenticated.postValue(Resource(Status.ERROR, false, e.localizedMessage))
            }
        }
    }

    fun sendForgotPassword(email: String): LiveData<Resource<Boolean>> {
        return liveData(Dispatchers.IO) {
            try {
                Firebase.auth.sendPasswordResetEmail(email).await()
                emit(Resource.success(true))
            } catch (e: FirebaseAuthException) {
                Log.e("LoginViewModel: ", "Error when sending PWD Reset", e)
                emit(Resource(Status.ERROR, false, e.localizedMessage))
            }
        }
    }

    fun createUser(username: String, name: String, bio: String) {
        authenticated.postValue(Resource.loading(false))
        viewModelScope.launch(Dispatchers.IO) {
            var profileUrl = ""
            var error = false
            if (profilePicUri.value != null) {
                val value = UserRepository.setProfilePic(profilePicUri.value!!)
                if (value.status == Status.SUCCESS && value.data != null) {
                    profileUrl = value.data.toString()
                } else {
                    authenticated.postValue(Resource.error(value.message, false))
                    error = true
                }
            }
            if (!error) {
                try {
                    UserRepository.createProfile(Profile(Firebase.auth.currentUser?.uid!!, username, name, bio, profileUrl, 0, ServerValue.TIMESTAMP))
                    authenticated.postValue(Resource.success(true))
                } catch (e: FirebaseException) {
                    Log.e("FirebaseHelper", "Create User failed", e.cause)
                    authenticated.postValue(Resource(Status.ERROR, false, e.localizedMessage))
                }
            }
        }

    }

    fun userNameExists(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            usernameExists.postValue(Resource.loading(false))
            usernameExists.postValue(UserRepository.usernameExists(username))
        }
    }

    fun isFirebaseUserNull(): Boolean {
        return Firebase.auth.currentUser == null
    }

    fun setProfilePicUri(uri: String) {
        profilePicUri.postValue(Uri.parse(uri))
    }


}