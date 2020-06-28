/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.takePhoto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.databinding.FragmentTakePhotoBinding
import java.io.File
import java.util.concurrent.Executor

class TakePhotoFragment : Fragment(), CameraXConfig.Provider, Executor {
    private lateinit var bind: FragmentTakePhotoBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider
    private var captureInstance: ImageCapture? = null
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

        bind.switchCamera.setOnClickListener {
            orientation = if (orientation == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT
            try {
                bindPreview(cameraProvider)
            } catch (e: Exception) {Log.e("TakePhotoFragment", e.message, e)}
        }

        bind.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        bind.cameraCaptureButton.setOnClickListener {
            takePicture()
            Toast.makeText(context, "Capturing Image", Toast.LENGTH_SHORT).show()
        }

    }
    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        cameraProvider.unbindAll()
        val preview : Preview = Preview.Builder().build()
        captureInstance = ImageCapture.Builder()
                .setTargetRotation(bind.previewView.display.rotation)
                .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(orientation)
                .build()

        preview.setSurfaceProvider(bind.previewView.createSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, captureInstance)

    }

    private fun takePicture() {
        if (captureInstance == null) return
        val savedFile = File.createTempFile("tempProfilePic", ".jpg", requireContext().cacheDir);
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(savedFile).build()
        captureInstance?.takePicture(outputFileOptions,this, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                activity?.runOnUiThread {
                    bind.testImage.visibility = View.VISIBLE
                    Log.e("IMAGE", savedFile.toUri().toString())
                    bind.testImage.setImageURI(savedFile.toUri())
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.GOT_PHOTO_KEY, savedFile.toUri().toString())
                    findNavController().navigateUp()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("TakePhotoFragment", exception.message, exception)
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        })
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    override fun execute(command: Runnable) {
        command.run()
    }
}