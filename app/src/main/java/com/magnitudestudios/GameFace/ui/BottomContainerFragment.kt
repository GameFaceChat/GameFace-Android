/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentBottomNavBinding
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BottomContainerFragment : Fragment() {
    private lateinit var bind: FragmentBottomNavBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentBottomNavBinding.inflate(inflater, container, false)
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHost = requireActivity().findNavController(R.id.containerNavHost)
        bind.bottomNav.setupWithNavController(navHost)
        //Handle Profile Pic changes
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.GOT_PHOTO_KEY)?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    //Get the uri from cropped photo
                    val uri = Uri.parse(it)
                    //Uri after saving to firebase
                    val remoteUri = UserRepository.setProfilePic(uri)
                    //Successful save
                    if (remoteUri.status == Status.SUCCESS){
                        mainViewModel.profile.postValue(Resource.success(mainViewModel.profile.value?.data?.apply {
                            profilePic = remoteUri.data.toString()
                        }))
                        UserRepository.updateUserProfile(mutableMapOf(Profile::profilePic.name to remoteUri.data.toString()))
                        Log.e("SAVED", "Saved new pfp")
                    }
                    else {  //Error occurred while uploading
                        Log.e("Bottom Container", remoteUri.message ?: "ERROR")
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Something unexpected happened!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                findNavController().currentBackStackEntry?.savedStateHandle?.set(Constants.GOT_PHOTO_KEY, null)
            }
        })

    }
}