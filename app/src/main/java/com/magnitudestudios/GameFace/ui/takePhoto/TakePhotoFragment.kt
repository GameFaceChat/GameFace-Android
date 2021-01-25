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

package com.magnitudestudios.GameFace.ui.takePhoto

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
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
import com.magnitudestudios.GameFace.aspectRatio
import com.magnitudestudios.GameFace.databinding.FragmentTakePhotoBinding
import java.io.File
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * Take photo fragment
 *
 * @constructor Create empty Take photo fragment
 */
class TakePhotoFragment : Fragment(), CameraXConfig.Provider, Executor {
    private val TAG = "TakePhotoFragment"
    private lateinit var bind: FragmentTakePhotoBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraProvider: ProcessCameraProvider
    private var captureInstance: ImageCapture? = null
    private var orientation = CameraSelector.LENS_FACING_FRONT
    private var camera: Camera? = null
    private var cameraSelector: CameraSelector? = null

    private lateinit var mGestureDetector: GestureDetector

    private var flash = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentTakePhotoBinding.inflate(inflater, container, false)
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCameraProvider()

        bind.switchCamera.setOnClickListener {
            switchCamera()
        }

        bind.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        bind.cameraCaptureButton.setOnClickListener {
            takePicture()
        }

        bind.flashBtn.setOnClickListener {
            if (camera == null) return@setOnClickListener
            flash = !flash
            if (flash) {
                bind.flashBtn.background = requireContext().getDrawable(R.drawable.baseline_flash_on_black_24dp)
                captureInstance?.flashMode = ImageCapture.FLASH_MODE_ON
            }
            else {
                bind.flashBtn.background = requireContext().getDrawable(R.drawable.baseline_flash_off_black_24dp)
                captureInstance?.flashMode = ImageCapture.FLASH_MODE_OFF
            }

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

    private fun setUpCamera(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()      //Unbind all for now
        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { bind.previewView.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = bind.previewView.display.rotation

        val preview: Preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

        captureInstance = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(bind.previewView.display.rotation)
                .build()
        if (flash) captureInstance?.flashMode = ImageCapture.FLASH_MODE_ON
        cameraSelector = CameraSelector.Builder()
                .requireLensFacing(orientation)
                .build()

        preview.setSurfaceProvider(bind.previewView.surfaceProvider)
        try {
            camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector!!, preview, captureInstance)
            setUpTapToFocus()
            mGestureDetector = GestureDetector(requireContext(), GestureListener())
            bind.previewView.setOnTouchListener { _, event -> mGestureDetector.onTouchEvent(event) }
        } catch (e: Exception) {

            Log.e(TAG, "Camera setup failed", e)
        }
    }

    private fun switchCamera() {
        orientation = if (orientation == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK
        else CameraSelector.LENS_FACING_FRONT
        try {
            setUpCamera(cameraProvider)
        } catch (e: Exception) {Log.e("TakePhotoFragment", e.message, e)}
    }

    private fun takePicture() {
        if (captureInstance == null) return
        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = orientation == CameraSelector.LENS_FACING_FRONT
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/${getString(R.string.app_name)}")
            }
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(requireContext().applicationContext.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                .setMetadata(metadata).build()
        captureInstance?.takePicture(outputFileOptions, this, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                activity?.runOnUiThread {
                    val action = TakePhotoFragmentDirections.actionTakePhotoFragmentToCropPhotoFragment(outputFileResults.savedUri.toString())
                    findNavController().navigate(action)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                activity?.runOnUiThread {
                    Log.e("TakePhotoFragment", exception.message, exception)
                    onError()
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTapToFocus() {
        if (camera == null || cameraSelector == null) return
        bind.previewView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                if (event.action != MotionEvent.ACTION_UP) return true
                val factory = bind.previewView.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action: FocusMeteringAction = FocusMeteringAction.Builder(point).build()
                camera!!.cameraControl.startFocusAndMetering(action)
                return true
            }

        })

    }

    private fun onError() {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }


    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    override fun execute(command: Runnable) {
        command.run()
    }

    /**
     * Gesture listener
     *
     * @constructor Create empty Gesture listener
     */
    inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            if (cameraSelector == null) return true
            val factory = bind.previewView.meteringPointFactory
            val point = factory.createPoint(event.x, event.y)
            val action: FocusMeteringAction = FocusMeteringAction.Builder(point).build()
            camera?.cameraControl?.startFocusAndMetering(action)
            return true
        }

        // event when double tap occurs
        override fun onDoubleTap(e: MotionEvent): Boolean {
            switchCamera()
            return true
        }
    }


}