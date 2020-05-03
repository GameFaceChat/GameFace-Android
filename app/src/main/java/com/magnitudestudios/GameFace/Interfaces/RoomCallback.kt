package com.magnitudestudios.GameFace.Interfaces

import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO

interface RoomCallback {
    fun offerReceived(session: SessionInfoPOJO?)
    fun answerReceived(session: SessionInfoPOJO?)
    fun newParticipantJoined(s: String?)
    fun iceServerReceived(iceCandidate: IceCandidatePOJO?)
    fun participantLeft(s: String?)
    fun onJoinedRoom(b: Boolean)
    fun onCreateRoom()
    fun onLeftRoom()
}