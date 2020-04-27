package com.magnitudestudios.GameFace;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.magnitudestudios.GameFace.Network.FirebaseHelper;

public class GameFace extends Application {
    public FirebaseHelper firebaseHelper;
    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseHelper = FirebaseHelper.getInstance();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("APPLICATION", "onLowMemory");
    }
}
