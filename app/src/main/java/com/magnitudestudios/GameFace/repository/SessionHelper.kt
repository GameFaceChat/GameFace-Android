/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus
import com.magnitudestudios.GameFace.pojo.VideoCall.EmitMessage
import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO
import kotlinx.coroutines.tasks.await
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SessionHelper {
    private var groupMembers = 0
    var uid = ""
    private var connectionListener: ChildEventListener? = null
    var currentRoom: String? = null
        private set
    var started: Boolean = false
    var initiator: Boolean

    private const val TAG = "SessionHelper"


    fun listenForMessages(callback: RoomCallback) {
        if (currentRoom == null) return
        connectionListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.e(TAG, "DATA: " + dataSnapshot.value)
                val emitMessage = dataSnapshot.getValue(EmitMessage::class.java)
                if (emitMessage == null) {
                    Log.e(TAG, "onChildAdded: NULL MESSAGE")
                    return
                }
                if (emitMessage.fromUID == uid || (emitMessage.toUID != uid && emitMessage.toUID != Constants.ALL_MEMBERS)) return
                val gson = Gson()
                when (emitMessage.type) {
                    Constants.JOINED_KEY -> {
                        callback.newParticipantJoined(emitMessage.fromUID)
                        groupMembers += 1
                    }
                    Constants.LEFT_KEY -> {
                        callback.participantLeft(emitMessage.fromUID)
                        groupMembers -= 1
                    }
                    Constants.OFFER_KEY -> callback.offerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    Constants.ANSWER_KEY -> callback.answerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    Constants.ICE_CANDIDATE_KEY -> callback.iceServerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), IceCandidatePOJO::class.java))
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.message)
            }
        }
        Firebase.database
                .getReference(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.CONNECT_PATH)
                .addChildEventListener(connectionListener!!)
    }



    private fun sendMessage(toUID: String, type: String, data: Any?): Task<Void?> {
        return Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.CONNECT_PATH)
                .push().setValue(EmitMessage(uid, toUID, type, data))
    }

    suspend fun createRoom(callback: RoomCallback, uid: String): String {
        initiator = true
        this.uid = uid
        currentRoom = Firebase.database.reference.child(Constants.ROOMS_PATH).push().key
        addMember(uid, currentRoom!!, MemberStatus.ACCEPTED)
        sendMessage(Constants.ALL_MEMBERS, Constants.JOINED_KEY, this.uid).await()
        listenForMessages(callback)
        return currentRoom!!
    }

    suspend fun joinRoom(roomName: String, callback: RoomCallback, uid: String): String {
        initiator = false
        this.uid = uid
        currentRoom = roomName
        sendMessage(Constants.ALL_MEMBERS, Constants.JOINED_KEY, uid).await()
        listenForMessages(callback)
        updateMemberStatus(uid, roomName, MemberStatus.ACCEPTED)
        return roomName
    }

    suspend fun leaveRoom(callback: RoomCallback) {
        if (currentRoom != null && connectionListener != null) {
            Firebase.database.getReference(Constants.ROOMS_PATH)
                    .child(currentRoom!!)
                    .child(Constants.CONNECT_PATH)
                    .removeEventListener(connectionListener!!)
            Log.e("REMOVED", "LISTENER")
            connectionListener = null
            if (FirebaseHelper.exists(Constants.ROOMS_PATH, currentRoom!!)) {
                try {
                    sendMessage(Constants.ALL_MEMBERS, Constants.LEFT_KEY, uid).await()
                    callback.onLeftRoom()
                    if (groupMembers == 1) closeRoom()
                    groupMembers = 0
                    currentRoom = null
                } catch (e: Exception) {

                }
            }
        }
    }

    suspend fun closeRoom(): Boolean {
        return try {
            Firebase.database.reference.child(Constants.ROOMS_PATH).child(currentRoom!!).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun sendOffer(session: SessionDescription, toUID: String) {
        sendMessage(toUID, Constants.OFFER_KEY, SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun sendAnswer(session: SessionDescription, toUID: String) {
        sendMessage(toUID, Constants.ANSWER_KEY, SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun sendIceCandidate(iceCandidate: IceCandidate, toUID: String) {
        sendMessage(toUID, Constants.ICE_CANDIDATE_KEY, IceCandidatePOJO(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp, iceCandidate.serverUrl))
    }

    fun addMember(uid: String, roomID: String, memberStatus: MemberStatus = MemberStatus.CALLING) {
        Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(roomID)
                .child(Constants.MEMBERS_PATH)
                .child(uid)
                .setValue(Member(uid = uid, roomID = roomID, memberStatus = memberStatus.name))
    }

    private fun updateMemberStatus(uid: String, roomID: String, status: MemberStatus) {
        Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(roomID)
                .child(Constants.MEMBERS_PATH)
                .child(uid)
                .child(Member::memberStatus.name)
                .setValue(status.name)
    }

    suspend fun getAllMembers(roomID: String) : List<Member> {
        val members = mutableListOf<Member>()
        FirebaseHelper.getValue(Constants.ROOMS_PATH, roomID, Constants.MEMBERS_PATH)?.children?.forEach {data ->
            data.getValue(Member::class.java)?.let {
                if (it.uid != Firebase.auth.currentUser!!.uid) members.add(it)
            }
        }
        return members
    }

    fun denyCall(uid: String, roomID: String){
        updateMemberStatus(uid, roomID, MemberStatus.UNAVAILABLE)
    }

    fun acceptCall(uid: String, roomID: String) {
        updateMemberStatus(uid, roomID, MemberStatus.ACCEPTED)
    }

    init {
        initiator = false
    }

}