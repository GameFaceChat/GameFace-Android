package com.magnitudestudios.GameFace.pojo;

public class IceCandidatePOJO {
    public String sdpMid;
    public int sdpMLineIndex;
    public String sdp;
    public String serverUrl;

    public IceCandidatePOJO() {}

    public IceCandidatePOJO(String sdpMid, int sdpMLineIndex, String sdp, String serverUrl) {
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdp = sdp;
        this.serverUrl = serverUrl;
    }
}
