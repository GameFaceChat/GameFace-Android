package com.magnitudestudios.GameFace;

import android.Manifest;

public class Constants {

    public static final int ALL_PERMISSIONS = 101;

    public static final int STATE_COMPLETED = 200;
    public static final int STATE_URL_FAILED = 201;

    public static final int STATE_CONNECTED = 1;
    public static final int STATE_FAILED = -1;
    public static final int STATE_DISCONNECTED = -2;

    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
}
