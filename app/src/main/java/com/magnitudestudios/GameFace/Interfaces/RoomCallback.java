package com.magnitudestudios.GameFace.Interfaces;

import com.magnitudestudios.GameFace.pojo.IceCandidatePOJO;
import com.magnitudestudios.GameFace.pojo.SessionInfoPOJO;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public interface RoomCallback {
    void offerReceived(SessionInfoPOJO session);
    void answerReceived(SessionInfoPOJO session);
    void newParticipantJoined(String s);
    void iceServerReceived(IceCandidatePOJO iceCandidate);
    void participantLeft(String s);
    void onJoinedRoom(boolean b);
    void onCreateRoom();
    void onLeftRoom();

}
