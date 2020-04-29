package com.magnitudestudios.GameFace.Network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Interfaces.ReferenceExists
import com.magnitudestudios.GameFace.Interfaces.RoomCallback
import com.magnitudestudios.GameFace.pojo.EmitMessage
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SessionHelper {
    private val TAG = "Session Helper"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseUser: FirebaseUser?
    private var childEventListener: ChildEventListener? = null
    var currentRoom: String? = null
    private var currentUser: String? = null
    var started: Boolean
    var initiator: Boolean

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
                Log.i(TAG, "onChildAdded: " + emitMessage.type)
                if (emitMessage.userID == null) {
                    Log.e(TAG, "onChildAdded: " + "NO USER ID")
                } else if (emitMessage.userID == currentUser) {
                    return
                }
                val gson = Gson()
                when (emitMessage.type) {
                    "JOINED" -> callback.newParticipantJoined(emitMessage.userID)
                    "LEFT" -> {
                        callback.participantLeft(emitMessage.userID)
                    }
                    "OFFER" -> callback.offerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    "ANSWER" -> callback.answerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                    "ICECANDIDATE" -> callback.iceServerReceived(gson.fromJson(gson.toJsonTree(emitMessage.data), IceCandidatePOJO::class.java))
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.message)
            }
        }
        mDatabase.getReference("rooms").child(currentRoom!!).addChildEventListener(childEventListener as ChildEventListener)
    }

    private fun sendMessage(type: String, data: Any?): Task<Void?> {
        return mDatabase.reference.child("rooms").child(currentRoom!!).push().setValue(EmitMessage(currentUser, type, data))
    }

    fun createRoom(roomName: String?, username: String?, callback: RoomCallback) {
        currentRoom = roomName
        currentUser = username
        sendMessage("JOINED", username).addOnCompleteListener { task: Task<Void?>? ->
            callback.onCreateRoom()
            readMessage(callback)
        }
    }

    fun closeRoom() {
        mDatabase.reference.child("rooms").child(currentRoom!!).removeValue()
    }

    fun joinRoom(roomName: String?, username: String?, callback: RoomCallback) {
        currentRoom = roomName
        currentUser = username
        sendMessage("JOINED", username).addOnCompleteListener { task: Task<Void?>? ->
            callback.onJoinedRoom(true)
            readMessage(callback)
        }
    }

    fun leaveRoom(callback: RoomCallback) {
        if (currentUser != null && currentRoom != null) {
            mDatabase.getReference("rooms").child(currentRoom!!).removeEventListener(childEventListener!!)
            referenceExists(ReferenceExists { b: Boolean, data: DataSnapshot? ->
                if (b) {
                    sendMessage("LEFT", currentUser).addOnCompleteListener { task: Task<Void?>? -> callback.onLeftRoom() }
                }
            }, "rooms", currentRoom!!)
        }
    }

    fun sendOffer(session: SessionDescription) {
        sendMessage("OFFER", SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun sendAnswer(session: SessionDescription) {
        sendMessage("ANSWER", SessionInfoPOJO(session.type.toString(), session.description))
    }

    fun sendIceCandidate(iceCandidate: IceCandidate) {
        sendMessage("ICECANDIDATE", IceCandidatePOJO(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp, iceCandidate.serverUrl))
    }


    private fun referenceExists(exists: ReferenceExists, vararg path: String) {
        var databaseReference = mDatabase.reference
        for (ref in path) databaseReference = databaseReference.child(ref)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                exists.referenceExists(dataSnapshot.exists(), dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    init {
        firebaseUser = mAuth.currentUser
        started = false
        initiator = false
    }
}