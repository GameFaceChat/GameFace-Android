/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.VideoCall.EmitMessage
import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO
import kotlinx.coroutines.tasks.await
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SessionHelper {
    private var groupMembers = 0
    var uid = ""
    private var childEventListener: ChildEventListener? = null
    var currentRoom: String? = null
        private set
    var started: Boolean = false
    var initiator: Boolean

    private const val TAG = "SessionHelper"


    fun readMessage(callback: RoomCallback) {
        if (currentRoom == null) return
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.e(TAG, "DATA: " + dataSnapshot.value)
                val emitMessage = dataSnapshot.getValue(EmitMessage::class.java)
                if (emitMessage == null) {
                    Log.e(TAG, "onChildAdded: NULL MESSAGE")
                    return
                }
                if (emitMessage.userID == uid) {
                    return
                }
                val gson = Gson()
                when (emitMessage.type) {
                    Constants.JOINED_KEY -> {
                        callback.newParticipantJoined(emitMessage.userID)
                        groupMembers += 1
                    }
                    Constants.LEFT_KEY -> {
                        callback.participantLeft(emitMessage.userID)
                        groupMembers -= 1
                    }
                    Constants.OFFER_KEY -> callback.offerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    Constants.ANSWER_KEY -> callback.answerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    Constants.ICE_CANDIDATE_KEY -> callback.iceServerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), IceCandidatePOJO::class.java))
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
                .addChildEventListener(childEventListener!!)
    }

    private fun sendMessage(type: String, data: Any?): Task<Void?> {
        return Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.CONNECT_PATH)
                .push().setValue(EmitMessage(uid, type, data))
    }

    suspend fun createRoom(callback: RoomCallback, uid: String) : String {
        initiator = true
        this.uid = uid
        currentRoom = Firebase.database.reference.child(Constants.ROOMS_PATH).push().key
        addToMembers(this.uid)
        sendMessage(Constants.JOINED_KEY, this.uid).await()
        callback.onCreateRoom()
        readMessage(callback)
        return currentRoom!!
    }

    suspend fun joinRoom(roomName: String, callback: RoomCallback, uid: String) : String {
        initiator = false
        this.uid = uid
        currentRoom = roomName
        addToMembers(this.uid)
        sendMessage(Constants.JOINED_KEY, uid).await()
        callback.onJoinedRoom(true)
        readMessage(callback)
        return roomName
    }

    suspend fun addToMembers(uid: String) {
        Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.MEMBERS_PATH)
                .child(uid)
                .setValue(Constants.JOINED_KEY).await()
    }

    suspend fun leaveRoom(callback: RoomCallback) {
        if (currentRoom != null && childEventListener != null) {
            Firebase.database.getReference(Constants.ROOMS_PATH)
                    .child(currentRoom!!)
                    .child(Constants.CONNECT_PATH)
                    .removeEventListener(childEventListener!!)
            Log.e("REMOVED","LISTENER")
            childEventListener = null
            if (FirebaseHelper.exists(Constants.ROOMS_PATH, currentRoom!!)) {
                try {
                    sendMessage(Constants.LEFT_KEY, uid).await()
                    callback.onLeftRoom()
                    if (groupMembers == 1) closeRoom()
                    groupMembers = 0
                    currentRoom = null
                } catch (e: Exception) {

                }
            }
        }
    }

    suspend fun closeRoom() : Boolean {
        return try {
            Firebase.database.reference.child(Constants.ROOMS_PATH).child(currentRoom!!).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun sendOffer(session: SessionDescription) {
        sendMessage(Constants.OFFER_KEY, SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun sendAnswer(session: SessionDescription) {
        sendMessage(Constants.ANSWER_KEY, SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        sendMessage(Constants.ICE_CANDIDATE_KEY, IceCandidatePOJO(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp, iceCandidate.serverUrl))
    }

    init {
        initiator = false
    }

}