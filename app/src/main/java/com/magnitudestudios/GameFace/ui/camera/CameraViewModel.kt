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

import android.app.Application
import android.se.omapi.Session
import android.util.Log
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.MemberCallback
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.network.DownloadSinglePack
import com.magnitudestudios.GameFace.network.HTTPRequest
import com.magnitudestudios.GameFace.notifyObserver
import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.VideoCall.*
import com.magnitudestudios.GameFace.repository.SessionRepository
import com.magnitudestudios.GameFace.repository.UserRepository
import com.magnitudestudios.GameFace.utils.CustomSdpObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Camera view model
 *
 * @constructor
 *
 * @param application
 */
class CameraViewModel(application: Application) : AndroidViewModel(application), RoomCallback, MemberCallback {
    private val connectedRoom = MutableLiveData<String>()

    val members = MutableLiveData<MutableList<Member>>(mutableListOf())
    val newMember = MutableLiveData<Member>()
    val changedMember = MutableLiveData<Int>()

    val connections = MutableLiveData<ConcurrentHashMap<String, PeerConnection>>(ConcurrentHashMap())

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

    /**
     * Add peer
     *
     * @param uid
     * @param peer
     */
    fun addPeer(uid: String, peer: PeerConnection) {
        if (!connections.value!!.containsKey(uid)) {
            connections.value?.put(uid, peer)
            connections.notifyObserver()
        }

    }

    /**
     * Called when an ICE candidate is found, and must be sent to a peer
     *
     * @param uid
     * @param iceCandidate
     */
    fun onIceCandidate(uid: String, iceCandidate: IceCandidate) {
        if (!SessionRepository.currentRoom.isNullOrEmpty()) SessionRepository.sendIceCandidate(iceCandidate, uid)
    }


    /**
     * Initiate connection witha peer (create offer)
     *
     * @param uid
     */
    fun initiateConnection(uid: String) {
        val peer = connections.value?.get(uid) ?: return
        val sdpConstraints = MediaConstraints()
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        sdpConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        peer.createOffer(object : CustomSdpObserver("createOffer") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peer.setLocalDescription(CustomSdpObserver("setLocalDesc"), sessionDescription)
                SessionRepository.sendOffer(sessionDescription, toUID = uid)
            }
        }, sdpConstraints)
    }

    /**
     * Create a room
     *
     * @param calls
     */
    fun createRoom(vararg calls: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val roomID = SessionRepository.createRoom(
                    this@CameraViewModel,
                    this@CameraViewModel,
                                    Firebase.auth.uid!!)
            connectedRoom.postValue(roomID)
            connectionStatus.postValue(Resource.success(true))
            calls.forEach {
                user -> addMember(user, roomID)
            }
        }
    }

    /**
     * Add a member to the current room
     *
     * @param uid
     * @param roomID
     */
    fun addMember(uid: String, roomID : String? = connectedRoom.value) {
        if (!roomID.isNullOrEmpty()) {
            SessionRepository.addMember(uid, roomID)
        }
    }

    /**
     * Join a room
     *
     * @param roomID
     */
    fun joinRoom(roomID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectedRoom.postValue(SessionRepository.joinRoom(
                    roomID,
                    this@CameraViewModel,
                    this@CameraViewModel,
                    Firebase.auth.uid!!))
            connectionStatus.postValue(Resource.success(true))
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
                SessionRepository.sendAnswer(sessionDescription, fromUID)
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
        removeParticipant(uid)
        Log.e("PARTICIPANT LEFT", "UID: $uid")
    }

    override fun onLeftRoom() {

    }

    /**
     * Remove a participant
     *
     * @param uid
     */
    @Synchronized
    fun removeParticipant(uid: String) {
        connections.value!!.remove(uid)
    }

    /**
     * Used to hang up the call
     *
     */
    @Synchronized
    fun hangUp() {
        if (connections.value != null) {
            val peers = connections.value!!.keys
            for (uid in peers) {
                try {
                    connections.value!![uid]?.close()
                    removeParticipant(uid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            connections.notifyObserver()
            connectionStatus.value = Resource.nothing()
        }
        SessionRepository.removeListeners()
        Log.e("LEAVE", "TASK")
        if (SessionRepository.currentRoom != null) {
            val leaveTask = OneTimeWorkRequestBuilder<SessionRepository.LeaveRoomWorker>()
                    .setInputData(workDataOf(Constants.ROOM_ID_KEY to SessionRepository.currentRoom)).build()
            WorkManager.getInstance(getApplication()).enqueue(leaveTask)
            SessionRepository.currentRoom = null
        }
    }

    override fun onNewMember(member: Member) {
        viewModelScope.launch {
            member.profile = UserRepository.getUserProfileByUID(member.uid)
            members.value?.add(member)
            newMember.value = member
        }
    }

    override fun onMemberStatusChanged(uid: String, newStatus: MemberStatus) {
        val index = members.value?.map { it.uid }?.indexOf(uid) ?: -1

        if (index > 0) {
            members.value?.get(index)?.memberStatus = newStatus.name
            changedMember.value = index
        }
    }


}