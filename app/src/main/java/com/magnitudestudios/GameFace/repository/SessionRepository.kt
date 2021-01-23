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

package com.magnitudestudios.GameFace.repository

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.addListener
import com.magnitudestudios.GameFace.callbacks.MemberCallback
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.doOnChildAdded
import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.VideoCall.EmitMessage
import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object SessionRepository {
    var uid = Firebase.auth.currentUser?.uid
    private var connectionListener: ChildEventListener? = null
    private var memberListener: ChildEventListener? = null

    var currentRoom: String? = null

    private const val TAG = "SessionHelper"

    private fun listenForMessages(callback: RoomCallback) {
        if (currentRoom == null) return
        connectionListener = Firebase.database
                .getReference(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.CONNECT_PATH)
                .addListener (childAdded = { dataSnapshot ->
                    Log.e(TAG, "DATA: " + dataSnapshot.value)
                    val emitMessage = dataSnapshot.getValue(EmitMessage::class.java)
                    if (emitMessage == null) {
                        Log.e(TAG, "onChildAdded: NULL MESSAGE")
                        return@addListener
                    }
                    if (emitMessage.fromUID == uid || (emitMessage.toUID != uid && emitMessage.toUID != Constants.ALL_MEMBERS)) return@addListener
                    val gson = Gson()
                    when (emitMessage.type) {
                        Constants.JOINED_KEY -> callback.newParticipantJoined(emitMessage.fromUID)
                        Constants.LEFT_KEY -> callback.participantLeft(emitMessage.fromUID)
                        Constants.OFFER_KEY -> callback.offerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                        Constants.ANSWER_KEY -> callback.answerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), SessionInfoPOJO::class.java))
                        Constants.ICE_CANDIDATE_KEY -> callback.iceServerReceived(emitMessage.fromUID, gson.fromJson(gson.toJsonTree(emitMessage.data), IceCandidatePOJO::class.java))
                    }
                },
                onCancelled = {error -> Log.e("ERROR CONNECTION", error.message, error.toException()) })
    }

    private fun listenToMemberStatus(callback: MemberCallback) {
        memberListener = Firebase.database
                .getReference(Constants.ROOMS_PATH)
                .child(currentRoom!!)
                .child(Constants.MEMBERS_PATH)
                .addListener  (onCancelled = {
                    error -> Log.e("ERROR CONNECTION", error.message, error.toException())
                }, childAdded = {
                    Log.e("GOT MEMBER ADDED", it.value.toString())
                    val newMember = it.getValue(Member::class.java)
                    if (newMember != null) callback.onNewMember(newMember)
                }, childChanged = {snapshot, _ ->
                    Log.e("GOT MEMBER CHANGED", snapshot.value.toString())
                    val memberChanged = snapshot.getValue(Member::class.java)
                    if (memberChanged != null) {
                        callback.onMemberStatusChanged(memberChanged.uid, MemberStatus.valueOf(memberChanged.memberStatus))
                    }
                })
    }



    private fun sendMessage(toUID: String, type: String, data: Any?, roomID: String = currentRoom!!): Task<Void?> {
        return Firebase.database.reference
                .child(Constants.ROOMS_PATH)
                .child(roomID)
                .child(Constants.CONNECT_PATH)
                .push().setValue(EmitMessage(uid!!, toUID, type, data))
    }

    suspend fun createRoom(roomCallback: RoomCallback, memberCallback: MemberCallback, uid: String): String {
        this.uid = uid
        currentRoom = Firebase.database.reference.child(Constants.ROOMS_PATH).push().key
        addMember(uid, currentRoom!!, MemberStatus.ACCEPTED)
        sendMessage(Constants.ALL_MEMBERS, Constants.JOINED_KEY, this.uid).await()
        listenForMessages(roomCallback)
        listenToMemberStatus(memberCallback)
        return currentRoom!!
    }

    suspend fun joinRoom(roomName: String, roomCallback: RoomCallback, memberCallback: MemberCallback, uid: String): String {
        this.uid = uid
        currentRoom = roomName
        updateMemberStatus(uid, roomName, MemberStatus.ACCEPTED)
        sendMessage(Constants.ALL_MEMBERS, Constants.JOINED_KEY, uid).await()
        listenForMessages(roomCallback)
        listenToMemberStatus(memberCallback)
        return roomName
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
                .setValue(Member(uid = uid, memberStatus = memberStatus.name))
    }

    fun removeListeners() {
        if (currentRoom == null) return
        val reference = Firebase.database.getReference(Constants.ROOMS_PATH)
                .child(currentRoom!!)
        if (connectionListener != null) {
            reference.child(Constants.CONNECT_PATH).removeEventListener(connectionListener!!)
            connectionListener = null
        }
        if (memberListener != null) {
            reference.child(Constants.MEMBERS_PATH).removeEventListener(memberListener!!)
            memberListener = null
        }
    }

    fun updateMemberStatus(uid: String, roomID: String, status: MemberStatus) {
        Log.e("UPDATING STATUS", "$uid : ${status.name}")
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

    //Requires RoomID
    class LeaveRoomWorker(appContext : Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
        override suspend fun doWork(): Result {
            val room = inputData.getString(Constants.ROOM_ID_KEY)
            if (room != null) {
                if (FirebaseHelper.exists(Constants.ROOMS_PATH, room)) {
                    try {
                        updateMemberStatus(Firebase.auth.currentUser?.uid!!, room, MemberStatus.UNAVAILABLE)
                        sendMessage(Constants.ALL_MEMBERS, Constants.LEFT_KEY, uid, room).await()
                    } catch (e: Exception) {
                        Log.e("ERROR WHILE LEAVING", e.message, e)
                        return Result.failure()}
                }
            }
            return Result.success()
        }
    }

}