/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentCameraBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.utils.CustomPeerConnectionObserver
import com.magnitudestudios.GameFace.views.MovableSurfaceView
import org.webrtc.*

class CameraFragment : BaseFragment(), View.OnClickListener {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var videoCapturer: VideoCapturer? = null
    private lateinit var videoSource: VideoSource
    private lateinit var localAudioTrack: AudioTrack
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var audioConstraints: MediaConstraints
    private lateinit var videoConstraints: MediaConstraints
    private lateinit var sdpConstraints: MediaConstraints
//    private var iceServers: ArrayList<PeerConnection.IceServer> = ArrayList();
    private lateinit var rootEglBase: EglBase
    private lateinit var audioSource: AudioSource

    private lateinit var bind: FragmentCameraBinding

    private lateinit var audioManager: AudioManager

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: CameraViewModel

    private var videoViews = hashMapOf<String, MovableSurfaceView>()

    private val args: CameraFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(requireContext().applicationContext).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        bind = FragmentCameraBinding.inflate(inflater)
        mainViewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }!!
        viewModel = activity?.run { ViewModelProvider(this).get(CameraViewModel::class.java) }!!
        rootEglBase = EglBase.create()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        bind.localVideo.init(rootEglBase.eglBaseContext, null)
        bind.remoteVideo.init(rootEglBase.eglBaseContext, null)

        bind.localVideo.setZOrderMediaOverlay(true)
        bind.remoteVideo.setZOrderMediaOverlay(false)

        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        startCamera()

        observeConnection()
        observeIceConnection()
        observeNewPeers()

        bind.addMember.setOnClickListener {
            findNavController().navigate(R.id.action_cameraFragment_to_addMembersDialog)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private fun observeConnection() {
        viewModel.connectionStatus.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.ERROR -> connectionFailed(it.message)
                Status.LOADING -> setLoading(true)
                else -> setLoading(false)
            }
        })

        viewModel.connections.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
        })
    }

    private fun observeIceConnection() {
        viewModel.iceServers.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (args.roomID.isNotEmpty()) {
                    viewModel.joinRoom(args.roomID)
                }
                else if (args.callUserUID.isNotEmpty()) {
                    viewModel.createRoom(args.callUserUID)
                }
            }
        })
    }

    private fun observeNewPeers() {
        viewModel.newPeer.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                createPeerConnection(it)
                if (Firebase.auth.currentUser?.uid!! > it) viewModel.initiateConnection(it)
            }
        })
    }

    private fun startCamera() {
        Log.e(TAG, "startCamera: " + "STARTING CAMERA")
//        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory()

        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        videoCapturer = createCameraCapturer(Camera1Enumerator(false))!!

        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.eglBaseContext)
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer!!.isScreencast)
        videoCapturer!!.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)

        //Create a VideoSource instance
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)
        videoSource.adaptOutputFormat(720, 480, 30)
        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)

        videoCapturer?.startCapture(720, 480, 30)

        //create surface renderer, init it and add the renderer to the track
        bind.localVideo.setMirror(true)
        bind.localVideo.setEnableHardwareScaler(true)
        bind.remoteVideo.setEnableHardwareScaler(true)

        localVideoTrack.addSink(bind.localVideo)
    }

    private fun createPeerConnection(uid: String) {
        val rtcConfig = PeerConnection.RTCConfiguration(viewModel.iceServers.value).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            keyType = PeerConnection.KeyType.ECDSA
        }

        val peer = peerConnectionFactory.createPeerConnection(rtcConfig, object : CustomPeerConnectionObserver(uid, "localPeerCreation") {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                super.onIceCandidate(iceCandidate)
                viewModel.onIceCandidate(peerUID, iceCandidate)
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                gotPeerStream(peerUID, mediaStream)
            }
        })

        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        peer?.let {
            it.addStream(stream)
            viewModel.addPeer(uid, it)
        }
    }

    private fun gotPeerStream(peerUID: String, stream: MediaStream ) {
        Log.e(TAG, "gotRemoteStream: " + "GOT REMOTE STREAM")
        //we have remote video stream. add to the renderer.
        activity?.runOnUiThread {
            val videoTrack = stream.videoTracks[0]
            val videoView : MovableSurfaceView
            if (!videoViews.containsKey(peerUID)) {
                val params = FrameLayout.LayoutParams(400, 700)
                videoView = MovableSurfaceView(context).apply {
                    setZOrderMediaOverlay(true)
                    layoutParams = params
                    init(rootEglBase.eglBaseContext, null)
                }
                videoViews[peerUID] = videoView
                bind.root.addView(videoView)

            } else {
                videoView = videoViews[peerUID]!!
            }
            try {
                videoTrack.addSink(videoView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            transitionConnected()
        }
    }

    private fun transitionConnected() {
        bind.progressBar.visibility = View.GONE
        bind.localVideo.setCalling()
    }

    private fun transitionDisconnected() {
        bind.localVideo.setLocal()
    }

    private fun setLoading(b: Boolean) {
        bind.progressBar.visibility = if (b) View.VISIBLE else View.GONE
    }
    private fun connectionFailed(message: String? = null) {
        activity?.runOnUiThread {
            bind.progressBar.visibility = View.GONE
            if (!message.isNullOrEmpty()) Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun disconnect() {
        viewModel.hangUp()
        transitionDisconnected()
        try {
            videoCapturer?.stopCapture()
            bind.remoteVideo.release()
            bind.localVideo.release()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
    }

    override fun onStop() {
        super.onStop()
        disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("DESTROYING", "DESTROYED")
        disconnect()
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        // Trying to find a front facing camera!
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) return videoCapturer
            }
        }

        // We were not able to find a front cam. Look for other cameras
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) return videoCapturer
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disconnect()
        rootEglBase.release()
    }


    companion object {
        private const val TAG = "CAMERA"
    }
}