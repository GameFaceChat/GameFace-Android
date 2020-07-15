package com.magnitudestudios.GameFace.callbacks

import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO

interface RoomCallback {
    fun offerReceived(fromUID: String, session: SessionInfoPOJO)
    fun answerReceived(fromUID: String, session: SessionInfoPOJO?)
    fun newParticipantJoined(uid: String)
    fun iceServerReceived(fromUID: String, iceCandidate: IceCandidatePOJO)
    fun participantLeft(uid: String)
    fun onLeftRoom()
}