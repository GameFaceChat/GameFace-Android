package com.magnitudestudios.GameFace.callbacks

import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO

interface RoomCallback {
    fun offerReceived(session: SessionInfoPOJO)
    fun answerReceived(session: SessionInfoPOJO?)
    fun newParticipantJoined(s: String?)
    fun iceServerReceived(iceCandidate: IceCandidatePOJO)
    fun participantLeft(s: String?)
    fun onJoinedRoom(b: Boolean)
    fun onCreateRoom()
    fun onLeftRoom()
}