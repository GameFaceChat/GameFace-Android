/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.Activities

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.Bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : BasePermissionsActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navhost = findNavController(R.id.mainNavHost)
        binding.mainBottomNav.setupWithNavController(navhost)

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}