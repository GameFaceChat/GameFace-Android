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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentBottomNavBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel

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
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(viewLifecycleOwner, Observer {
//            if (it != null) Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            else Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
//        })
    }
}