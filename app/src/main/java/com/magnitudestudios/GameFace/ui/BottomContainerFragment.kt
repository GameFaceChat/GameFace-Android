/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentBottomNavBinding

class BottomContainerFragment : Fragment() {
    private lateinit var bind: FragmentBottomNavBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentBottomNavBinding.inflate(inflater, container, false)
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHost = requireActivity().findNavController(R.id.containerNavHost)
        bind.bottomNav.setupWithNavController(navHost)

    }
}