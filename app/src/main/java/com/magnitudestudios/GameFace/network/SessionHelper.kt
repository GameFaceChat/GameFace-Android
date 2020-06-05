package com.magnitudestudios.GameFace.network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.ReferenceExists
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.EmitMessage
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SessionHelper {
    private var mAuth: FirebaseAuth
    private val mDatabase: FirebaseDatabase
    private var groupMembers = 0
    var username = ""
    private var childEventListener: ChildEventListener? = null
    var currentRoom: String? = null
        private set
    var started: Boolean
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
                if (emitMessage.userID == null) {
                    Log.e(TAG, "onChildAdded: " + "NO USER ID")
                } else if (emitMessage.userID == username) {
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
        mDatabase.getReference("rooms").child(currentRoom!!).addChildEventListener(childEventListener!!)
    }

    private fun sendMessage(type: String, data: Any?): Task<Void?> {
        return mDatabase.reference.child("rooms").child(currentRoom!!).push().setValue(EmitMessage(username, type, data))
    }

    private fun createRoom(callback: RoomCallback) {
        initiator = true
        sendMessage(Constants.JOINED_KEY, username).addOnCompleteListener {
            callback.onCreateRoom()
            readMessage(callback)
        }
    }

    private fun joinRoom(callback: RoomCallback) {
        initiator = false
        sendMessage(Constants.JOINED_KEY, username).addOnCompleteListener {
            callback.onJoinedRoom(true)
            readMessage(callback)
        }
    }

    fun call(roomName: String?, callback: RoomCallback) {
        currentRoom = roomName
        groupMembers += 1
        referenceExists(ReferenceExists { b, _ ->
            if (b) {
                joinRoom(callback)
            } else {
                createRoom(callback)
            }
        }, "rooms", currentRoom!!)
    }

    fun leaveRoom(callback: RoomCallback) {
        if (currentRoom != null) {
            mDatabase.getReference("rooms").child(currentRoom!!).removeEventListener(childEventListener!!)
            referenceExists(ReferenceExists { b: Boolean, _: DataSnapshot? ->
                if (b) {
                    sendMessage(Constants.LEFT_KEY, username).addOnCompleteListener {
                        callback.onLeftRoom()
                        if (groupMembers == 1) closeRoom()
                        groupMembers = 0
                        currentRoom = null
                    }
                }
            }, "rooms", currentRoom!!)
        }
    }

    private fun closeRoom() {
        mDatabase.reference.child("rooms").child(currentRoom!!).removeValue()
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

    fun getUsername() {
        if (mAuth.currentUser == null) return
        referenceExists(ReferenceExists { b, data ->
            if (b) {
                this.username = data.value as String
            }
            else {
                this.username = "RANDOM_USER_"+ (Math.random()*100000).toInt()
            }
        }, Constants.USERS_PATH, mAuth.uid!!,"username")
    }


    init {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        started = false
        initiator = false
        getUsername()
    }
}