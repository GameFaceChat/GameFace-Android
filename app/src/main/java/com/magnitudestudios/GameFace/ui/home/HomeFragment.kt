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

package com.magnitudestudios.GameFace.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.magnitudestudios.GameFace.aspectRatio
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentHomeBinding

/**
 * Home fragment
 *
 * @constructor Create empty Home fragment
 */
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
        cameraProviderFuture.addListener({
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

        preview.setSurfaceProvider(bind.previewCamera.surfaceProvider)
        try {
            camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, preview)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Camera setup failed", e)
        }
        return false
    }
}