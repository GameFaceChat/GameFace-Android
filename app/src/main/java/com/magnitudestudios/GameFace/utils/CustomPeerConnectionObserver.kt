package com.magnitudestudios.GameFace.utils

import android.util.Log
import org.webrtc.*
import org.webrtc.PeerConnection.*

open class CustomPeerConnectionObserver(var peerUID: String, logTag: String) : Observer {
    private var logTag : String = this.javaClass.canonicalName + " " + logTag + " UID: $peerUID"
    override fun onSignalingChange(signalingState: SignalingState) {
        Log.e(logTag, "onSignalingChange() called with: signalingState = [$signalingState]")
    }

    override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
        Log.e(logTag, "onIceConnectionChange() called with: iceConnectionState = [$iceConnectionState]")
    }

    override fun onIceConnectionReceivingChange(b: Boolean) {
        Log.e(logTag, "onIceConnectionReceivingChange() called with: b = [$b]")
    }

    override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
        Log.e(logTag, "onIceGatheringChange() called with: iceGatheringState = [$iceGatheringState]")
    }

    override fun onIceCandidate(iceCandidate: IceCandidate) {
        Log.e(logTag, "onIceCandidate() called with: iceCandidate = [$iceCandidate]")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
        Log.e(logTag, "onIceCandidatesRemoved() called with: iceCandidates = [$iceCandidates]")
    }

    override fun onAddStream(mediaStream: MediaStream) {
        Log.e(logTag, "onAddStream() called with: mediaStream = [$mediaStream]")
    }

    override fun onRemoveStream(mediaStream: MediaStream) {
        Log.e(logTag, "onRemoveStream() called with: mediaStream = [$mediaStream]")
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        Log.e(logTag, "onDataChannel() called with: dataChannel = [$dataChannel]")
    }

    override fun onRenegotiationNeeded() {
        Log.e(logTag, "onRenegotiationNeeded() called")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver, mediaStreams: Array<MediaStream>) {
        Log.e(logTag, "onAddTrack() called with: rtpReceiver = [$rtpReceiver] and [$mediaStreams]")
    }

}