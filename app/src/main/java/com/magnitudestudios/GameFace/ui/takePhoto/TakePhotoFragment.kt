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
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import com.magnitudestudios.GameFace.databinding.FragmentTakePhotoBinding
import java.lang.Exception

class TakePhotoFragment : Fragment(), CameraXConfig.Provider {
    private lateinit var bind: FragmentTakePhotoBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider

    private var orientation = CameraSelector.LENS_FACING_FRONT
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentTakePhotoBinding.inflate(inflater, container, false)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

        bind.cameraCaptureButton.setOnClickListener {
            orientation = if (orientation == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT
            try {
                bindPreview(cameraProvider)
            } catch (e: Exception) {}
        }

    }
    fun bindPreview(cameraProvider : ProcessCameraProvider) {
        cameraProvider.unbindAll()
        val preview : Preview = Preview.Builder().build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(orientation)
                .build()

        preview.setSurfaceProvider(bind.previewView.createSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}