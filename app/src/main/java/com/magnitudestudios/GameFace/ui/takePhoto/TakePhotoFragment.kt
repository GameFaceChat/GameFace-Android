/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.takePhoto

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentTakePhotoBinding
import java.io.File
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TakePhotoFragment : Fragment(), CameraXConfig.Provider, Executor {
    private val TAG = "TakePhotoFragment"
    private lateinit var bind: FragmentTakePhotoBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider
    private var captureInstance: ImageCapture? = null
    private var orientation = CameraSelector.LENS_FACING_FRONT
    private var camera: Camera?=null
    private var cameraSelector: CameraSelector? = null

    private var torch = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentTakePhotoBinding.inflate(inflater, container, false)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCameraProvider()

        bind.switchCamera.setOnClickListener {
            orientation = if (orientation == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
            else CameraSelector.LENS_FACING_FRONT
            try {
                setUpCamera(cameraProvider)
            } catch (e: Exception) {Log.e("TakePhotoFragment", e.message, e)}
        }

        bind.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        bind.cameraCaptureButton.setOnClickListener {
            takePicture()
        }

        bind.flashBtn.setOnClickListener {
            if (camera == null) return@setOnClickListener
            torch = !torch
            if (torch) bind.flashBtn.background = requireContext().getDrawable(R.drawable.baseline_flash_on_black_24dp)
            else bind.flashBtn.background = requireContext().getDrawable(R.drawable.baseline_flash_off_black_24dp)

            camera!!.cameraControl.enableTorch(torch)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(Constants.GOT_PHOTO_KEY)?.observe(viewLifecycleOwner, Observer {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(Constants.GOT_PHOTO_KEY, it)
            findNavController().navigateUp()
        })

    }

    private fun getCameraProvider() {
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            setUpCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setUpCamera(cameraProvider : ProcessCameraProvider) {
        cameraProvider.unbindAll()      //Unbind all for now
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { bind.previewView.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = bind.previewView.display.rotation

        val preview : Preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        captureInstance = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(bind.previewView.display.rotation)
                .build()

        cameraSelector = CameraSelector.Builder()
                .requireLensFacing(orientation)
                .build()

        preview.setSurfaceProvider(bind.previewView.createSurfaceProvider())
        try {

            camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, preview, captureInstance)
            setUpTapToFocus()
        }
        catch (e: Exception) {
            Log.e(TAG, "Camera setup failed", e)
        }
    }

    private fun takePicture() {
        if (captureInstance == null) return
        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = orientation == CameraSelector.LENS_FACING_FRONT
        }

        // Create output options object which contains file + metadata
        val savedFile = File.createTempFile("tempProfilePic", ".jpg", requireContext().cacheDir);
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(savedFile)
                .setMetadata(metadata)
                .build()
        captureInstance?.takePicture(outputFileOptions,this, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                activity?.runOnUiThread {
                    val action = TakePhotoFragmentDirections.actionTakePhotoFragmentToCropPhotoFragment(savedFile.toUri().toString())
                    findNavController().navigate(action)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("TakePhotoFragment", exception.message, exception)
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTapToFocus() {
        if (camera == null || cameraSelector == null) return
        bind.previewView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_UP) {
                    return true
                }
                val factory = bind.previewView.createMeteringPointFactory(cameraSelector!!)
                val point = factory.createPoint(event.x, event.y)
                val action: FocusMeteringAction = FocusMeteringAction.Builder(point).build()
                camera!!.cameraControl.startFocusAndMetering(action)
                return true
            }

        })

    }


    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - (4.0/3.0)) <= abs(previewRatio - (16.0/9.0))) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


}