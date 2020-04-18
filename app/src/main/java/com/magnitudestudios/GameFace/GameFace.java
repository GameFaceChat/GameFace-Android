package com.magnitudestudios.GameFace;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class GameFace extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("APPLICATION", "onLowMemory");
    }
}
