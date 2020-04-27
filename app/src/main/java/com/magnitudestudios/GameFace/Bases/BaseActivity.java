package com.magnitudestudios.GameFace.Bases;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.magnitudestudios.GameFace.GameFace;
import com.magnitudestudios.GameFace.Network.FirebaseHelper;

public class BaseActivity extends AppCompatActivity {
    public FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseHelper = ((GameFace) getApplicationContext()).firebaseHelper;
    }
}
