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

package com.magnitudestudios.GameFace.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentBottomNavBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Bottom container fragment
 *
 * @constructor Create empty Bottom container fragment
 */
class BottomContainerFragment : Fragment() {
    private lateinit var bind: FragmentBottomNavBinding
    private lateinit var mainViewModel: MainViewModel
    private var colorAnimation: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentBottomNavBinding.inflate(inflater, container, false)
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        val navHost = requireActivity().findNavController(R.id.containerNavHost)
        bind.bottomNav.setupWithNavController(navHost)
        bind.bottomNav.setOnNavigationItemSelectedListener {
            animatedSelected(it.itemId)
            return@setOnNavigationItemSelectedListener NavigationUI.onNavDestinationSelected(it, navHost)
        }
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

    private fun animatedSelected(itemId: Int) {
        if (itemId == R.id.homeFragment) {
            animateBackground(R.color.colorAccent, R.color.colorPrimary)
        }
        else {
            animateBackground(R.color.colorPrimary, R.color.colorAccent)
        }
    }

    private fun animateBackground (fromColorRes: Int, toColourRes: Int) {
        colorAnimation?.cancel()
        val colorFrom = ContextCompat.getColor(requireContext(), fromColorRes)
        val colorTo = ContextCompat.getColor(requireContext(), toColourRes)
        colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation?.duration = 1000
        colorAnimation?.addUpdateListener { animator -> bind.bottomNav.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation?.start()
    }

    override fun onResume() {
        super.onResume()
        animatedSelected(bind.bottomNav.selectedItemId)
    }
}