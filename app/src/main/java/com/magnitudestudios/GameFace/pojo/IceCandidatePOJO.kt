package com.magnitudestudios.GameFace.pojo

class IceCandidatePOJO {
    @JvmField
    var sdpMid: String? = null
    @JvmField
    var sdpMLineIndex = 0
    @JvmField
    var sdp: String? = null
    var serverUrl: String? = null

    constructor() {}
    constructor(sdpMid: String?, sdpMLineIndex: Int, sdp: String?, serverUrl: String?) {
        this.sdpMid = sdpMid
        this.sdpMLineIndex = sdpMLineIndex
        this.sdp = sdp
        this.serverUrl = serverUrl
    }
}