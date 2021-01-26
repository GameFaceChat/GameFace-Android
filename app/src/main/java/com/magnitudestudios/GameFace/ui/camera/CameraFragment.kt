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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentCameraBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.utils.CustomPeerConnectionObserver
import com.magnitudestudios.GameFace.views.MovableScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.webrtc.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Camera fragment
 *
 * @constructor Create empty Camera fragment
 */
class CameraFragment : BaseFragment(), View.OnClickListener {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var videoCapturer: VideoCapturer? = null

    //Local stream setup variables
    private lateinit var videoSource: VideoSource
    private lateinit var audioSource: AudioSource

    private lateinit var localAudioTrack: AudioTrack
    private lateinit var localVideoTrack: VideoTrack

    private var localStream : MediaStream? = null

    private lateinit var audioConstraints: MediaConstraints
    private lateinit var videoConstraints: MediaConstraints

    private lateinit var rootEglBase: EglBase

    private lateinit var bind: FragmentCameraBinding

    private lateinit var audioManager: AudioManager

    private lateinit var mainViewModel: MainViewModel
    private val viewModel: CameraViewModel by navGraphViewModels(R.id.videoCallGraph)

    //Maps the Peer UIDs to Video screens
    private var videoViews : ConcurrentHashMap<String, MovableScreen> = ConcurrentHashMap()

    private lateinit var membersAdapter : MemberStatusAdapter

    private val args: CameraFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Initialize the peer connection factory
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(requireContext().applicationContext).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        bind = FragmentCameraBinding.inflate(inflater)
        mainViewModel = activity?.run {
            ViewModelProvider(this).get(MainViewModel::class.java)
        }!!
        rootEglBase = EglBase.create()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        bind.localVideo.initialize(eglBase = rootEglBase, overlay = true, onTop = true)

        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        startCamera()

        observeConnection()
        observeIceConnection()
        observeNewPeers()
        observeMembers()

        //Set up the listeners for buttons and root surface
        bind.root.setOnClickListener {
            lifecycleScope.launch {
                bind.callingControls.animate().setDuration(5000).alpha(1.0f)
                delay(5000)
                bind.callingControls.animate().setDuration(5000).alpha(0f)
            }
        }
        bind.callingControls.setOnClickListener {Log.e("CLICKED","CLICKED")}

        bind.addMember.setOnClickListener {
            findNavController().navigate(R.id.action_cameraFragment_to_addMembersDialog)
        }

        bind.muteAudio.setOnClickListener {
            localStream?.let {
                it.audioTracks.first().setEnabled(!it.audioTracks.first().enabled())
            }
        }

        bind.muteVideo.setOnClickListener {
            localStream?.let {
                it.videoTracks.first().setEnabled(!it.videoTracks.first().enabled())
            }
        }

        bind.hangup.setOnClickListener {
            disconnect(true)
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

    /**
     * Observe members: updates the users UI when a member has been added/ their status
     * has changed.
     *
     */
    private fun observeMembers() {
        membersAdapter = MemberStatusAdapter(viewModel.members.value!!)
        bind.showMembers.adapter = membersAdapter
        bind.showMembers.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }.apply { orientation = LinearLayoutManager.HORIZONTAL }
        viewModel.changedMember.observe(viewLifecycleOwner, {
            membersAdapter.notifyItemChanged(it)
        })
        viewModel.newMember.observe(viewLifecycleOwner, Observer {
            membersAdapter.add(it)
            membersAdapter.notifyDataSetChanged()
        })
    }

    /**
     * Observe ICE connection
     * Called when the user's potential ICE servers from the API
     * have been received, and is ready to join a room,
     * or create a room
     *
     */
    private fun observeIceConnection() {
        viewModel.iceServers.observe(viewLifecycleOwner, {
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

    /**
     * Observe new peers: Called whenever a new peer has joined.
     * Creates the peer connection, and only send the offer if their UID is lexicographically
     * greater than the other peer's.
     *
     */
    private fun observeNewPeers() {
        viewModel.newPeer.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty() && it != Firebase.auth.currentUser!!.uid) {
                createPeerConnection(it)
                if (Firebase.auth.currentUser?.uid!! > it) viewModel.initiateConnection(it)
            }
        })
    }

    /**
     * Start the camera: sets up the camera and audio constraints and
     * stream. The local video track is the local stream from your camera
     *
     */
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
        bind.localVideo.surface.setMirror(true)
        bind.localVideo.surface.setEnableHardwareScaler(true)

        localVideoTrack.addSink(bind.localVideo.surface)

        localStream = peerConnectionFactory.createLocalMediaStream("102").apply {
            addTrack(localAudioTrack)
            addTrack(localVideoTrack)
        }

    }

    /**
     * Creates a peer connection with the specified UID
     *
     * @param uid   The UID of the peer
     */
    private fun createPeerConnection(uid: String) {
        //Configure the connection
        val rtcConfig = PeerConnection.RTCConfiguration(viewModel.iceServers.value).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            keyType = PeerConnection.KeyType.ECDSA
        }

        //Create the peer
        val peer = peerConnectionFactory.createPeerConnection(rtcConfig, object : CustomPeerConnectionObserver(uid, "localPeerCreation") {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                super.onIceCandidate(iceCandidate)
                viewModel.onIceCandidate(peerUID, iceCandidate)
            }

            override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
                super.onIceConnectionChange(iceConnectionState)
                activity?.runOnUiThread {
                    updateConnectionStatus(peerUID, iceConnectionState)
                }
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                gotPeerStream(peerUID, mediaStream)
            }
        })
        peer?.let {
            it.addStream(localStream)
            viewModel.addPeer(uid, it)      //Add the peer to the viewModel
        }
    }

    /**
     * Update connection status UI
     *
     * @param uid                   The uid of the member
     * @param iceConnectionState    The new ICE connection state
     */
    private fun updateConnectionStatus(uid: String, iceConnectionState: PeerConnection.IceConnectionState) {
        when (iceConnectionState) {
            PeerConnection.IceConnectionState.NEW -> {
                getScreen(uid)
            }
            PeerConnection.IceConnectionState.CHECKING -> {
                videoViews[uid]?.setLoading(true)
            }
            PeerConnection.IceConnectionState.CONNECTED, PeerConnection.IceConnectionState.COMPLETED -> {
                if (stillConnectedMembers()) bind.chronometer.start()
                else bind.chronometer.stop()
                videoViews[uid]?.setLoading(false)
            }
            PeerConnection.IceConnectionState.DISCONNECTED -> {
                videoViews[uid]?.setDisconnected()
                if (stillConnectedMembers()) bind.chronometer.start()
                else bind.chronometer.stop()
            }
            PeerConnection.IceConnectionState.CLOSED, PeerConnection.IceConnectionState.FAILED -> {
                if (stillConnectedMembers()) bind.chronometer.start()
                else bind.chronometer.stop()
                removePeer(uid)
            }
        }
    }

    /**
     * Helper function to check whether there are other members in the call still
     *
     * @return
     */
    private fun stillConnectedMembers() : Boolean {
        viewModel.connections.value?.forEach {
            if (it.value.iceConnectionState() == PeerConnection.IceConnectionState.COMPLETED || it.value.iceConnectionState() == PeerConnection.IceConnectionState.CONNECTED) {
                return true
            }
        }
        return false
    }

    /**
     * Removes a peer (UI)
     *
     * @param uid
     */
    @Synchronized
    private fun removePeer(uid: String) {
        videoViews[uid]?.surface?.release()
        Log.e("REMOVING VIEW: ", uid)
        bind.peersVideoLayout.removeView(videoViews[uid])
        videoViews.remove(uid)
        viewModel.removeParticipant(uid)

        if (videoViews.isEmpty()) transitionDisconnected()
    }


    /**
     * Got a peer stream from the specified UID and is ready to displayed on a screen
     *
     * @param peerUID   The UID of the peer
     * @param stream    The MediaStream of the peer to attach to screen surface
     */
    private fun gotPeerStream(peerUID: String, stream: MediaStream ) {
        Log.e(TAG, "gotRemoteStream: " + "GOT REMOTE STREAM")
        //we have remote video stream. add to the renderer.
        activity?.runOnUiThread {
            val videoTrack = stream.videoTracks[0]
            try {
                videoTrack.addSink(getScreen(peerUID).surface)
                transitionConnected()
            } catch (e: Exception) {
                e.printStackTrace()
                connectionFailed("Error getting stream")
            }
        }
    }

    /**
     * Helper function to retrieve/create a screen for peer given their UID
     *
     * @param peerUID   The UID of the peer
     * @return  A movable screen object
     */
    private fun getScreen(peerUID: String) : MovableScreen {
        val videoView : MovableScreen
        if (!videoViews.containsKey(peerUID)) {
            val params = FrameLayout.LayoutParams(400, 700)
            videoView = MovableScreen(context = requireContext(), overlay = false).apply {
                initialize(rootEglBase, false)
                layoutParams = params
            }
            videoViews[peerUID] = videoView
            bind.peersVideoLayout.addView(videoView)

        } else {
            videoView = videoViews[peerUID]!!
        }
        return videoView
    }

    /**
     * Transitions the local screen into connected mode
     *
     */
    private fun transitionConnected() {
        bind.localVideo.setCalling()
    }

    /**
     * Transition the local screen into disconnected mode
     *
     */
    private fun transitionDisconnected() {
        bind.localVideo.setLocal()
    }

    /**
     * Sets the screen into loading mode (ie when connecting)
     *
     * @param b - True when loading, false otherwise.
     */
    private fun setLoading(b: Boolean) {
        bind.localVideo.setLoading(b)

    }

    /**
     * Connection failed: called when there is an error connecting
     *
     * @param message
     */
    private fun connectionFailed(message: String? = null) {
        activity?.runOnUiThread {
            if (!message.isNullOrEmpty()) Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Disconnect the current user from all screens
     *
     * @param userDefined
     */
    private fun disconnect(userDefined: Boolean = false) {
        viewModel.hangUp()
        transitionDisconnected()
        try {
            videoCapturer?.stopCapture()
            bind.localVideo.surface.release()
            localStream?.dispose()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (userDefined) {
            findNavController().popBackStack()
        }
    }

    override fun onClick(v: View) {}

    override fun onDestroy() {
        super.onDestroy()
        Log.e("DESTROYING", "DESTROYED")
        audioManager.mode = AudioManager.MODE_NORMAL
        disconnect()
    }

    /**
     * Finds the camera devices available for the video stream
     *
     * @param enumerator
     * @return
     */
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
        bind.localVideo.surface.release()
        rootEglBase.release()
    }


    companion object {
        private const val TAG = "CAMERA"
    }
}