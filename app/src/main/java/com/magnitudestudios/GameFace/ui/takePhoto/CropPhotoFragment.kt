/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.takePhoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.magnitudestudios.GameFace.databinding.FragmentCropPfpBinding

class CropPhotoFragment : Fragment() {
    private lateinit var bind: FragmentCropPfpBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentCropPfpBinding.inflate(inflater, container, false)
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}