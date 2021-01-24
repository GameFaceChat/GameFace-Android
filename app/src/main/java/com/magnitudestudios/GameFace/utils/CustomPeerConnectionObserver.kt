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

package com.magnitudestudios.GameFace.utils

import android.util.Log
import org.webrtc.*
import org.webrtc.PeerConnection.*

/**
 * Custom peer connection observer
 *
 * @property peerUID
 * @constructor
 *
 * @param logTag
 */
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