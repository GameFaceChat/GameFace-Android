package com.magnitudestudios.GameFace

import android.Manifest

object Constants {
    const val ALL_PERMISSIONS = 101
    const val STATE_COMPLETED = 200
    const val STATE_URL_FAILED = 500
    const val STATE_CONNECTED = 1
    const val STATE_FAILED = -1
    const val STATE_DISCONNECTED = -2
    val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE
    )

    //Firebase paths
    const val ROOMS_PATH = "rooms"
    const val USERS_PATH = "users"
    const val STORE_PATH = "store"
    const val OWNED_PACKS = "owned_packs"

    const val CONNECT_PATH = "connection"
    const val PROFILE_PATH = "profiles"
    const val MEMBERS_PATH = "members"

    const val CHARADES_PATH = "charades"
    const val WOULD_YOU_RATHER_PATH = "would_you_rather"
    const val TRUTH_OR_DARE_PATH = "truth_or_dare"

    const val SHOWCASE_PATH = "showcase"

    const val SENT_PROFILES_PATH = "friendRequestsSent"
    const val ROOM_MEMBERS_KEY = "room_members"

    //Firebase signalling keywords
    const val JOINED_KEY = "JOINED"
    const val LEFT_KEY = "LEFT"
    const val OFFER_KEY = "OFFER"
    const val ANSWER_KEY = "ANSWER"
    const val ICE_CANDIDATE_KEY = "ICECANDIDATE"

    const val ALL_MEMBERS = "ALL"

    const val CALLING_KEY = "CALLING"
    const val UNAVAILABLE_KEY = "UNAVAILABLE"

    const val SIGN_IN_CLIENT = "SIGNED_IN_CLIENT"

    const val NOT_FINISHED = "NotFinished"

    //Users states
    const val STATE_DEFAULT = 0
    const val STATE_REQUESTED = 1
    const val STATE_FRIENDS = 2
    const val STATE_OWN_PROFILE = 3

    //Keys
    const val GOT_PHOTO_KEY = "gotPhotoFromUser"
    //Notification Vibration Pattern
    val VIBRATE_PATTERN = longArrayOf(1000, 1000, 1000, 1000, 1000, 100, 400)

    const val INSTALLED_PACKS_KEY = "installedPacksLocal"
    const val REMOTE_PACKS_KEY = "packs"
    //Intent Constants
    const val CALL_KEY = "GO_TO_CALLING"
    const val ROOM_ID_KEY = "ROOM_ID"

}