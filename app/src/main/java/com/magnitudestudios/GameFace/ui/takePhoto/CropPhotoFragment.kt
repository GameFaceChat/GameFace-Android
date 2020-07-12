/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.takePhoto

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentCropPfpBinding
import com.magnitudestudios.GameFace.utils.compressImage
import com.takusemba.cropme.OnCropListener
import java.io.File
import java.io.FileOutputStream

class CropPhotoFragment : Fragment() {
    private lateinit var bind: FragmentCropPfpBinding

    private val args: CropPhotoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentCropPfpBinding.inflate(inflater, container, false)
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.cropLayout.setUri(Uri.parse(args.photoUri))
        bind.cancelButton.setOnClickListener { findNavController().navigateUp() }
        bind.doneBtn.setOnClickListener {
            if (!bind.cropLayout.isOffFrame()) bind.cropLayout.crop()
        }
        bind.cropLayout.addOnCropListener(object : OnCropListener {
            override fun onFailure(e: Exception) {
                Toast.makeText(context, getString(R.string.cropping_failed), Toast.LENGTH_SHORT).show()
                Log.e("CropPhotoFragment", "onFailure:", e)
                findNavController().navigateUp()
            }

            override fun onSuccess(bitmap: Bitmap) {
                val savedFile = File.createTempFile("tempProfilePic", ".jpg", requireContext().cacheDir)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(savedFile))
                Log.e("After Crop ", savedFile.length().toString())
                compressImage(savedFile)
                Log.e("FINAL SIZE: ", savedFile.length().toString())
                findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.GOT_PHOTO_KEY, savedFile.toUri().toString())
                findNavController().navigateUp()
                bind.cropLayout
            }

        })

        bind.leftBtn.setOnClickListener { bind.cropLayout.rotateImage(-90f) }
        bind.rightBtn.setOnClickListener { bind.cropLayout.rotateImage(90f) }
        bind.flipBtn.setOnClickListener { bind.cropLayout.flipImage() }
    }

}