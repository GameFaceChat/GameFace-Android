package com.magnitudestudios.GameFace.network

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.EmitMessage
import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO
import kotlinx.coroutines.tasks.await
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SessionHelper {
    private var groupMembers = 0
    var username = ""
    private var listener: ListenerRegistration? = null
    var currentRoom: String? = null
        private set
    var started: Boolean
    var initiator: Boolean

    private const val TAG = "SessionHelper"


    fun readMessage(callback: RoomCallback) {
        if (currentRoom == null) return
        listener = Firebase.firestore.collection(Constants.ROOMS_PATH).document(currentRoom!!).collection(Constants.CONNECT_PATH).addSnapshotListener { snapshot, e ->
            val emitMessage = snapshot?.documents?.last()?.toObject(EmitMessage::class.java)
            if (emitMessage == null) {
                Log.e(TAG, "onChildAdded: NULL MESSAGE")
                return@addSnapshotListener
            }
            if (emitMessage.userID.isEmpty()) {
                Log.e(TAG, "onChildAdded: " + "NO USER ID")
            } else if (emitMessage.userID == username) {
                return@addSnapshotListener
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
    }

    private fun sendMessage(type: String, data: Any?): Task<DocumentReference?> {
        return Firebase.firestore.collection(Constants.ROOMS_PATH).document(currentRoom!!)
                .collection(Constants.CONNECT_PATH).add(EmitMessage(username, type, data))
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

    suspend fun call(roomName: String?, callback: RoomCallback) {
        currentRoom = roomName
        groupMembers += 1

        if (Firebase.firestore.collection(Constants.ROOMS_PATH).document(currentRoom!!).get().await().exists()) {
            joinRoom(callback)
        } else {
            createRoom(callback)
        }
    }

    suspend fun leaveRoom(callback: RoomCallback) {
        if (currentRoom != null) {
            listener?.remove()

            if (Firebase.firestore.collection(Constants.ROOMS_PATH).document(currentRoom!!).get().await().exists()) {
                try {
                    sendMessage(Constants.LEFT_KEY, username).await()
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
            Firebase.firestore.collection("rooms").document(currentRoom!!).delete().await()
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
        started = false
        initiator = false
    }
}