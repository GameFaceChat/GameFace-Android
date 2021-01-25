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

package com.magnitudestudios.GameFace.callbacks

import com.magnitudestudios.GameFace.pojo.VideoCall.IceCandidatePOJO
import com.magnitudestudios.GameFace.pojo.VideoCall.SessionInfoPOJO

/**
 * Callback for room events including WebRTC protocol events
 */
interface RoomCallback {
    /**
     * Called when an offer was received from a peer
     *
     * @param fromUID the user ID of the user who issued the offer
     * @param session data class of the session info
     */
    fun offerReceived(fromUID: String, session: SessionInfoPOJO)

    /**
     * Called when an answer was received from a peer
     *
     * @param fromUID the user ID of the user who issued the offer
     * @param session data class of the session info
     */
    fun answerReceived(fromUID: String, session: SessionInfoPOJO?)

    /**
     * Called when there is a new participant in the room
     * @param uid The user ID of the new participant
     */
    fun newParticipantJoined(uid: String)

    /**
     * Called when an ICE candidate is received
     * @param fromUID      ICE candidate received from this UID
     * @param iceCandidate Data class for the ICE Candidate
     */
    fun iceServerReceived(fromUID: String, iceCandidate: IceCandidatePOJO)

    /**
     * @param uid User ID of the participant who left the room
     */
    fun participantLeft(uid: String)

    /**
     * Called when the current user has left the room
     */
    fun onLeftRoom()
}