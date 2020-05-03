/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.magnitudestudios.GameFace.Bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.Interfaces.RoomCallback
import com.magnitudestudios.GameFace.Network.FirebaseHelper
import com.magnitudestudios.GameFace.Network.GetNetworkRequest
import com.magnitudestudios.GameFace.Network.SessionHelper
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.Utils.CustomPeerConnectionObserver
import com.magnitudestudios.GameFace.Utils.CustomSdpObserver
import com.magnitudestudios.GameFace.databinding.ActivityMainBinding
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.ServerInformation
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO
import org.webrtc.*
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BasePermissionsActivity(), View.OnClickListener, RoomCallback {
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var videoCapturer: VideoCapturer ?= null
    private lateinit var videoSource: VideoSource
    private lateinit var localVideoTrack: VideoTrack
    private lateinit var audioConstraints: MediaConstraints
    private lateinit var videoConstraints: MediaConstraints
    private lateinit var sdpConstraints: MediaConstraints
    private var localPeer: PeerConnection ?= null
    private var iceServers: ArrayList<PeerConnection.IceServer> = ArrayList();
    private lateinit var rootEglBase: EglBase
    private lateinit var audioSource: AudioSource
    private lateinit var localAudioTrack: AudioTrack

    private lateinit var binding: ActivityMainBinding

    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        iceServers = ArrayList()
        SessionHelper.getUsername()
        binding.connectButton.setOnClickListener(this)
        binding.disconnectButton.setOnClickListener(this)
        binding.mainButtonSignout.setOnClickListener(this)
        rootEglBase = EglBase.create()
        binding.localVideo.init(rootEglBase.eglBaseContext, null)
        binding.remoteVideo.init(rootEglBase.eglBaseContext, null)
        binding.localVideo.setZOrderMediaOverlay(true)
        binding.remoteVideo.setZOrderMediaOverlay(true)
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        startCamera()

    }

    //Network Handler
    @SuppressLint("HandlerLeak")
    private val mUrlHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.STATE_COMPLETED -> {
                    Log.d(TAG, "handleMessage: " + msg.obj as String)
                    val gson = Gson()
                    try {
                        val serverInformation = gson.fromJson(msg.obj as String, ServerInformation::class.java)
                        serverInformation.printAll()
                        addToIceServers(serverInformation)
                        SessionHelper.call("ROOM2", this@MainActivity)
                        onTryToStart()
                    } catch (e: JsonParseException) {
                        Log.e(TAG, "handleMessage: " + "COULD NOT PARSE JSON", e)
                    }
                }
                Constants.STATE_URL_FAILED -> {
                    Log.d(TAG, "handleMessage: " + msg.obj as String)
                    Toast.makeText(this@MainActivity, "Cannot Connect to Server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addToIceServers(serverInformation: ServerInformation) {
        for (iceServer in serverInformation.iceServers!!) {
            Log.e(TAG, "FOUND ICE SERVER: " + iceServer.url)
            var peerIceServer: PeerConnection.IceServer
            if (iceServer.credential == null) {
                peerIceServer = PeerConnection.IceServer.builder(iceServer.url).createIceServer()
                iceServers.add(peerIceServer)
            } else {
                peerIceServer = PeerConnection.IceServer.builder(iceServer.url)
                        .setUsername(iceServer.username)
                        .setPassword(iceServer.credential)
                        .createIceServer()
                iceServers.add(peerIceServer)
            }
        }
    }

    private fun startCamera() {
        Log.e(TAG, "startCamera: " + "STARTING CAMERA")
        //Initialize PeerConnectionFactory globals.
        val initializationOptions = InitializationOptions.builder(this).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(rootEglBase.eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory()

        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        //Now create a VideoCapturer instance. Callback methods are there if you want to do something!
        videoCapturer = createCameraCapturer(Camera1Enumerator(false))!!
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.eglBaseContext)
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer!!.isScreencast)
        videoCapturer!!.initialize(surfaceTextureHelper, this, videoSource.capturerObserver)

        //Create a VideoSource instance
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        videoCapturer!!.startCapture(720, 480, 30)

        //create surface renderer, init it and add the renderer to the track
        binding.localVideo.setMirror(true)
        binding.localVideo.setEnableHardwareScaler(true)
        binding.remoteVideo.setEnableHardwareScaler(true)
        localVideoTrack.addSink(binding.localVideo)
    }

    private fun create() {
        Log.e(TAG, "call: " + "CALLING")
        iceServers = ArrayList()
        getIceServers()
    }

    // Try moving getting ice servers before creating room

    private fun createPeerConnection() {
        Log.e(TAG, "createPeerConnection: $iceServers")
        val rtcConfig = RTCConfiguration(iceServers)
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, object : CustomPeerConnectionObserver("localPeerCreation") {
            override fun onIceCandidate(iceCandidate: IceCandidate) {
                super.onIceCandidate(iceCandidate)
                Log.e(TAG, "onIceCandidate: " + iceCandidate.sdp)
                SessionHelper.addIceCandidate(iceCandidate)
            }

            override fun onAddStream(mediaStream: MediaStream) {
                super.onAddStream(mediaStream)
                gotRemoteStream(mediaStream)
            }
        })
        val stream = peerConnectionFactory.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        localPeer!!.addStream(stream)
    }

    fun onTryToStart() {
        runOnUiThread {
            Log.e(TAG, "onTryToStart: " + "TRYING TO START")
            if (!SessionHelper.started) {
                createPeerConnection()
                SessionHelper.started = true
            }
        }
    }

    private fun gotRemoteStream(stream: MediaStream) {
        Log.e(TAG, "gotRemoteStream: " + "GOT REMOTE STREAM")
        //we have remote video stream. add to the renderer.
        runOnUiThread {
            val videoTrack = stream.videoTracks[0]
            binding.progressBar.visibility = View.GONE
            try {
                transitionConnected()
                videoTrack.addSink(binding.remoteVideo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun transitionConnected() {
        binding.remoteVideo.visibility = View.VISIBLE
        binding.localVideo.setCalling()
    }

    private fun transitionDisconnected() {
        binding.remoteVideo.visibility = View.GONE
        binding.localVideo.setLocal()
    }

    private fun getIceServers() {
        binding.progressBar.visibility = View.VISIBLE
        val a = GetNetworkRequest(mUrlHandler, getString(R.string.backend_cloud_function))
        a.execute()
    }

    private fun hangUp() {
        try {
            localPeer?.close()
            localPeer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SessionHelper.leaveRoom(this)
        SessionHelper.started = false
        transitionDisconnected()
    }

    private fun disconnect() {
        hangUp()
        try {
            videoCapturer?.stopCapture()
            binding.remoteVideo.release()
            binding.remoteVideo.release()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.connectButton -> create()
//            R.id.connectButton -> transitionConnected()
            R.id.disconnectButton -> hangUp()
//            R.id.disconnectButton -> transitionDisconnected()
            R.id.main_button_signout -> {
                FirebaseHelper.signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hangUp()
    }
    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }

    override fun onCreateRoom() {
        //Send offer
        Log.e(TAG, "CREATED ROOM")
        sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        localPeer!!.createOffer(object : CustomSdpObserver("localCreateOffer") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                Log.d(TAG, "onCreateSuccess234: " + sessionDescription.description)
                localPeer!!.setLocalDescription(CustomSdpObserver("localSetLocalDesc"), sessionDescription)
                //Send to peer
                SessionHelper.sendOffer(sessionDescription)
            }
        }, sdpConstraints)
    }

    override fun offerReceived(session: SessionInfoPOJO) {
        //Received offer
        Log.e(TAG, "offerReceived: " + session.description)
        localPeer!!.setRemoteDescription(CustomSdpObserver("gotOffer"), SessionDescription(SessionDescription.Type.fromCanonicalForm(session.type!!.toLowerCase(Locale.getDefault())), session.description))
        localPeer!!.createAnswer(object : CustomSdpObserver("localCreateAns") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(CustomSdpObserver("localSetLocal"), sessionDescription)
                SessionHelper.sendAnswer(sessionDescription)
            }
        }, MediaConstraints())
    }

    override fun answerReceived(session: SessionInfoPOJO?) {
        Log.e(TAG, "answerReceived: $session")
        localPeer!!.setRemoteDescription(CustomSdpObserver("localSetRemote"), SessionDescription(SessionDescription.Type.fromCanonicalForm(session?.type!!.toLowerCase(Locale.getDefault())), session.description))
    }

    override fun newParticipantJoined(s: String?) {
        Log.e(TAG, "newParticipantJoined: $s")
    }

    override fun iceServerReceived(iceCandidate: IceCandidatePOJO) {
        Log.e(TAG, "iceServerReceived: ")
        localPeer!!.addIceCandidate(IceCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp))
    }

    override fun participantLeft(s: String?) {
        Log.e(TAG, "participantLeft: $s")
        Toast.makeText(this@MainActivity, "PARTICIPANT LEFT", Toast.LENGTH_SHORT).show()
        hangUp()
    }

    override fun onJoinedRoom(b: Boolean) {
        Log.e(TAG, "onJoinedRoom: ")
    }

    override fun onLeftRoom() {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(this@MainActivity, "Left Room", Toast.LENGTH_SHORT).show()
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        // Trying to find a front facing camera!
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // We were not able to find a front cam. Look for other cameras
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}