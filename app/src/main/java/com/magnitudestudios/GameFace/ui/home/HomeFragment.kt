/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMorph.setOnClickListener { binding.btnMorph.startAnimation() }
        binding.goToCamera.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }
    }
}