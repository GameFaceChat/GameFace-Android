/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.network.HTTPRequest
import com.magnitudestudios.GameFace.notifyObserver
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.VideoCall.*
import com.magnitudestudios.GameFace.repository.SessionHelper
import com.magnitudestudios.GameFace.utils.CustomSdpObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.util.*

class CameraViewModel(application: Application) : AndroidViewModel(application), RoomCallback {
    private val connectedRoom = MutableLiveData<String>()

    private val members = MutableLiveData<List<Member>>()
    val connections = MutableLiveData<HashMap<String, PeerConnection>>(hashMapOf())

    val connectionStatus = MutableLiveData<Resource<Boolean>>(Resource.loading(false))

    val newPeer = MutableLiveData<String>()

    val iceServers = liveData(Dispatchers.IO) {
        val data = HTTPRequest.getServers()
        if (data.status == Status.ERROR) {
            emit(mutableListOf())
            connectionStatus.postValue(Resource.error("Error reaching servers", false))
            return@liveData
        }

        val tempIceServers = mutableListOf<PeerConnection.IceServer>()
        try {
            val serverInformation = Gson().fromJson(data.data, ServerInformation::class.java)
            for (iceServer in serverInformation.iceServers ?: mutableListOf<IceServer>()) {
                val peerIceServer: PeerConnection.IceServer = PeerConnection.IceServer.builder(iceServer.url)
                        .setUsername(iceServer.username)
                        .setPassword(iceServer.credential)
                        .createIceServer()

                tempIceServers.add(peerIceServer)
            }

        } catch (e: JsonParseException) {
            connectionStatus.postValue(Resource.error(e.message, false))
        }

        emit(tempIceServers)
    }

    fun addPeer(uid: String, peer: PeerConnection) {
        if (!connections.value!!.containsKey(uid)) {
            connections.value?.put(uid, peer)
            connections.notifyObserver()
        }

    }

    fun onIceCandidate(uid: String, iceCandidate: IceCandidate) {
        SessionHelper.sendIceCandidate(iceCandidate, uid)
    }


    fun initiateConnection(uid: String) {
        val peer = connections.value?.get(uid) ?: return
        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peer.createOffer(object : CustomSdpObserver("createOffer") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peer.setLocalDescription(CustomSdpObserver("setLocalDesc"), sessionDescription)
                SessionHelper.sendOffer(sessionDescription, toUID = uid)
            }
        }, sdpConstraints)
    }

    fun createRoom(vararg calls: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val roomID = SessionHelper.createRoom(this@CameraViewModel, Firebase.auth.uid!!)
            connectedRoom.postValue(roomID)
            connectionStatus.postValue(Resource.success(true))
            calls.forEach {
                user -> addMember(user, roomID)
            }
        }
    }

    fun addMember(uid: String, roomID : String? = connectedRoom.value) {
        if (!roomID.isNullOrEmpty()) {
            SessionHelper.addMember(uid, roomID)
        }
    }

    fun joinRoom(roomID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectedRoom.postValue(SessionHelper.joinRoom(roomID, this@CameraViewModel, Firebase.auth.uid!!))
            connectionStatus.postValue(Resource.success(true))
//            members.postValue(SessionHelper.getAllMembers(roomID))
        }
    }

    override fun offerReceived(fromUID: String, session: SessionInfoPOJO) {
        connections.value!![fromUID]?.setRemoteDescription(
                CustomSdpObserver("gotOffer"),
                SessionDescription(SessionDescription.Type.fromCanonicalForm(session.type!!.toLowerCase(Locale.getDefault())),
                        session.description))

        connections.value!![fromUID]?.createAnswer(object : CustomSdpObserver("localCreateAns") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                connections.value!![fromUID]!!.setLocalDescription(CustomSdpObserver("localSetLocal"), sessionDescription)
                SessionHelper.sendAnswer(sessionDescription, fromUID)
            }
        }, MediaConstraints())
    }

    override fun answerReceived(fromUID: String, session: SessionInfoPOJO?) {
        connections.value!![fromUID]?.setRemoteDescription(
                CustomSdpObserver("localSetRemote"),
                SessionDescription(SessionDescription.Type.fromCanonicalForm(session?.type!!.toLowerCase(Locale.getDefault())),
                        session.description))
    }

    override fun newParticipantJoined(uid: String) {
        newPeer.value = uid
    }

    override fun iceServerReceived(fromUID: String, iceCandidate: IceCandidatePOJO) {
        connections.value!![fromUID]?.addIceCandidate(IceCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp))
    }

    override fun participantLeft(uid: String) {
        connections.value?.remove(uid)
    }

    override fun onLeftRoom() {

    }

    fun hangUp() {
        if (connections.value == null) return
        val peers = connections.value!!.keys
        for (uid in peers) {
            try {
                connections.value!![uid]?.close()
                connections.value!![uid]?.close()
                connections.value!!.remove(uid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        connectionStatus.value = Resource.nothing()

        viewModelScope.launch {
            SessionHelper.leaveRoom(this@CameraViewModel)
        }
    }


}