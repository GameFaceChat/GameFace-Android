/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentCallFinishedBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.ui.main.MainViewModel

class CallFinished : BaseFragment() {
    lateinit var bind: FragmentCallFinishedBinding
    private val viewModel: CameraViewModel by navGraphViewModels(R.id.videoCallGraph)
    private lateinit var mainViewModel : MainViewModel
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentCallFinishedBinding.inflate(inflater, container, false)
        mainViewModel = requireActivity().run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd.adUnitId = getString(R.string.interstitial_after_calling)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToHome()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adRequest = AdRequest.Builder().build()
        bind.bannerAfterCall.loadAd(adRequest)
        mInterstitialAd.show()

        viewModel.members.observe(viewLifecycleOwner, Observer { mutableList ->
            if (mutableList.isNullOrEmpty()) {
                bind.membersNames.text = mainViewModel.profile.value?.data?.username
                Glide.with(this).loadProfile(mainViewModel.profile.value?.data?.profilePic, bind.profilePic)
            }
            else {
                bind.membersNames.text = mutableList.mapNotNull { it.profile?.username }.joinToString(", ")
                Glide.with(this).loadProfile(mutableList[0].profile?.profilePic, bind.profilePic)
            }
        })

        bind.goHome.setOnClickListener {
            goToHome()
        }
    }
    private fun goToHome() {
        findNavController().popBackStack(R.id.bottomContainerFragment, false)
    }
}