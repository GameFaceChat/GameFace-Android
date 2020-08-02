/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.magnitudestudios.GameFace.aspectRatio
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }
    lateinit var bind: FragmentHomeBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider
    private var orientation = CameraSelector.LENS_FACING_FRONT
    private var camera: Camera? = null
    private var cameraSelector: CameraSelector? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentHomeBinding.inflate(inflater)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            setUpCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

        bind.gameButton.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        bind.root.transitionToEnd()
    }

    override fun onPause() {
        super.onPause()
        bind.root.transitionToStart()
    }

    private fun setUpCamera(cameraProvider: ProcessCameraProvider) : Boolean {
        cameraProvider.unbindAll()      //Unbind all for now
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also {
            if (bind.previewCamera.display != null) bind.previewCamera.display.getRealMetrics(it)
            else return false
        }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = bind.previewCamera.display.rotation

        val preview: Preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        cameraSelector = CameraSelector.Builder()
                .requireLensFacing(orientation)
                .build()

        preview.setSurfaceProvider(bind.previewCamera.createSurfaceProvider())
        try {
            camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, preview)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Camera setup failed", e)
        }
        return false
    }
}